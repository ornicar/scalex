package scalex
package search

import db._

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

class Engine(
  indexes: List[index.Def],
  idsToDefs: Seq[String] => List[model.Def]
) extends scalaz.Validations {

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  def find(query: RawQuery): Validation[String, Results] = for {
    q ← query.analyze
    val adapter = InMemoryAdapter(indexes take 10)
    p ← validation(Paginator(adapter, query.currentPage, query.maxPerPage))
    defs = idsToDefs(p.currentPageResults map (_.id))
  } yield Results(p, defs)
}
