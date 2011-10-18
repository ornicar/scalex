package scalex
package http

import org.scalatra._
import scalex.search.{Search, Query}
import scalex.model.{Def, Block}
import collection.mutable.WeakHashMap
import javax.servlet.http._
import com.github.ornicar.paginator.Paginator

class MainServlet extends ScalatraServlet {

  val limit = 10

  val cache = WeakHashMap[Query, String]()

  get("/") {

    val response = getSome("q") match {
      case None => error("Empty query!")
      case Some(q) => {
        val query = Query(q, page, limit)
        cache.getOrElseUpdate(query, search(query))
      }
    }

    contentType = getSome("callback") match {
      case None => "application/json"
      case Some(_) => "application/javascript"
    }

    // handle jsonp
    getSome("callback") match {
      case None => response
      case Some(callback) => "%s(%s)" format (callback, response)
    }
  }

  notFound {
    <h1>Not found. Bummer.</h1>
  }

  def getSome(name: String): Option[String] = request.getParameter(name) match {
    case null | "" => None
    case x => Some(x)
  }

  def page: Int = getSome("page") map (_.toInt) getOrElse(1)

  def error(msg: String): String = JsonObject(Map("error" -> msg)).toString

  def search(query: Query): String = Search find query match {
    case Left(msg) => error(msg)
    case Right(paginator) => makeResult(query, paginator).toString
  }

  def makeResult(query: Query, paginator: Paginator[Def]): JsonObject = JsonObject(Map(
    "query" -> query.string,
    "results" -> (paginator.currentPageResults.toList map (JsonObject(_))),
    "nbResults" -> paginator.nbResults,
    "page" -> page,
    "nbPages" -> paginator.nbPages
  ))

}
