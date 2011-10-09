package ophir
package http

import org.scalatra._
import ophir.search.Search
import ophir.model.Def

class MainServlet extends ScalatraServlet {

  get("/") {
    search(request.getParameter("q"))
  }

  def search(query: String): String = (Search find query).right map (_.toList) match {
    case Left(msg) => msg
    case Right(results) =>
      "%d results for %s\n\n%s" format (results.length, query, render(results))
  }

  private def render(d: Def): String =
    d.name + "\n  " + d.toString + (
      if (d.comment != "") "\n  " + d.comment
      else ""
    )

  private def render(ds: List[Def]): String =
    ds map render map ("* "+) mkString "\n\n"
}
