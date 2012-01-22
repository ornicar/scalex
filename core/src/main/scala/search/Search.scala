package scalex
package search

import scalex.model.Def
import com.github.ornicar.paginator._
import dump.Store.read

object Search {

  type Result = Either[String, Paginator[Def]]

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  lazy val defs = read

  def find(query: Query): Result =
    paginator(query.currentPage, query.maxPerPage)

  private[this] def paginator(currentPage: Int, maxPerPage: Int) =
    Paginator(InMemoryAdapter(defs take 10), currentPage, maxPerPage)

}
