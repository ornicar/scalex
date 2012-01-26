package scalex
package search

import db.DefRepo
import com.github.ornicar.paginator._

object Search {

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  val defs: List[index.Def] = db.IndexRepo.findAll

  def find(query: Query): Either[String, Results] = {
    val adapter = InMemoryAdapter(defs take 10)
    for {
      p <- Paginator(adapter, query.currentPage, query.maxPerPage)
      defs = DefRepo byIds (p.currentPageResults map (_.id))
    } yield Results(p, defs)
  }
}
