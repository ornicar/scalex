package scalex
package http

import org.scalatra._
import scalex.search.{Engine, Query}
import scalex.model.{Def, Block}
import collection.mutable.WeakHashMap
import javax.servlet.http._
import com.github.ornicar.paginator.Paginator

class MainServlet extends ScalatraServlet {

  val limit = 15

  val cache = WeakHashMap[Query, String]()

  get("/") {

    val response = getSome("q") match {
      case None => error("Empty query!")
      case Some(q) => {
        val query = Query(q, page, limit)
        cache.getOrElseUpdate(query, search(query))
      }
    }

    getSome("callback") match {
      case None => contentType = "application/json"; response
      case Some(c) => contentType = "application/javascript"; "%s(%s)" format (c, response)
    }
  }

  notFound { <h1>Not found. Bummer.</h1> }

  def getSome(name: String): Option[String] = request.getParameter(name) match {
    case null | "" => None
    case x => Some(x)
  }

  def page: Int = getSome("page") map (_.toInt) getOrElse(1)

  def error(msg: String): String = JsonObject(Map("error" -> msg)).toString

  def search(query: Query): String = Engine find query match {
    case Left(msg) => error(msg)
    case Right(paginator) => Formatter(query.string, paginator).toString
  }

}
