package scalex
package search

import db.DefRepo

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

object Search extends scalaz.Validations {

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  val defs: List[index.Def] = db.IndexRepo.findAll

  def find(query: RawQuery): Validation[String, Results] = {
    val adapter = InMemoryAdapter(defs take 10)
    for {
      p <- validation(Paginator(adapter, query.currentPage, query.maxPerPage))
      defs = DefRepo byIds (p.currentPageResults map (_.id))
    } yield Results(p, defs)
  }
}
