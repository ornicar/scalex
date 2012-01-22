package scalex
package search

import scalex.model.Def
import com.github.ornicar.paginator._

object Search {

  type Result = Either[String, Paginator[Def]]

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  def find(query: Query): Result =
    paginator(query.currentPage, query.maxPerPage)

  private[this] def paginator(currentPage: Int, maxPerPage: Int) =
    Paginator(InMemoryAdapter(Seq(): Seq[Def]), currentPage, maxPerPage)

}

