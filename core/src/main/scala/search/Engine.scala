package scalex
package search

import db._

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

final class Engine(
    defs: List[index.Def],
    idsToDefs: Seq[String] ⇒ List[model.Def],
    incSearch: String ⇒ Unit) extends scalaz.Validations {

  lazy val scopeIndexes: Map[Scope, ScopeIndex] = IndexBuilder scopeIndexes defs
  lazy val scopes = scopeIndexes.keys.toList

  def find(query: RawQuery): Validation[String, Results] = {
    if (query.currentPage == 1) incSearch(query.string)
    for {
      q ← query.analyze
      index ← queryScopeIndex(q.scope) toSuccess "Invalid query scope"
      adapter = InMemoryAdapter(resolve(q.query, index))
      p ← validation(Paginator(adapter, query.currentPage, query.maxPerPage))
      defs = idsToDefs(p.currentPageResults map (_.id))
    } yield Results(p, defs)
  }

  def queryScopeIndex(queryScope: QueryScope): Option[ScopeIndex] = for {
    scope ← queryScope matchScope scopes
    scopeIndex ← scopeIndexes get scope
  } yield scopeIndex

  def resolve(query: Query, scopeIndex: ScopeIndex): List[index.Def] =
    query match {
      case TextQuery(tokens) ⇒
        defsOf(TokenSearch(scopeIndex.nameIndex, tokens.list).search)
      case SigQuery(sig) ⇒
        defsOf(SigSearch(scopeIndex.sigIndex, sig).search)
      case MixQuery(tokens, sig) ⇒ defsOf {
        val sigResults = SigSearch(scopeIndex.sigIndex, sig).search
        val txtResults = TokenSearch(scopeIndex.nameIndex, tokens.list).search
        txtResults collect {
          case (d, s) if sigResults contains d ⇒ (d, s + sigResults(d))
        }
      }
    }

  def defsOf(fragment: Map[index.Def, Score]): List[index.Def] = {
    fragment.toList sortBy {
      case (fun, score) ⇒ (-score, fun.qualifiedName, fun.decSize)
    } map (_._1)
  }
}
