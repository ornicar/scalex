package scalex
package search

import db._

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

final class Engine(
    defs: List[index.Def],
    idsToDefs: Seq[String] ⇒ List[model.Def]) extends scalaz.Validations {

  private val tokenIndex = TokenIndex(defs)
  private val sigIndex = SigIndex(defs)

  def find(query: RawQuery): Validation[String, Results] = for {
    q ← query.analyze
    adapter = InMemoryAdapter(resolve(q))
    p ← validation(Paginator(adapter, query.currentPage, query.maxPerPage))
    defs = idsToDefs(p.currentPageResults map (_.id))
  } yield Results(p, defs)

  def resolve(query: Query): List[index.Def] = query match {
    case TextQuery(tokens) ⇒
      defsOf(TokenSearch(tokenIndex, tokens.list).search)
    case SigQuery(sig) ⇒
      defsOf(SigSearch(sigIndex, sig).search)
    case MixQuery(tokens, sig) ⇒ defsOf {
      val sigResults = SigSearch(sigIndex, sig).search
      val txtResults = TokenSearch(tokenIndex, tokens.list).search
      txtResults collect {
        case (d, s) if sigResults contains d ⇒ (d, s + sigResults(d))
      }
    }
  }

  def defsOf(fragment: Fragment): List[index.Def] =
    fragment.toList sortWith {
      case (a, b) ⇒ a._2 > b._2
    } map (_._1)
}
