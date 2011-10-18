package scalex
package http

import org.scalatra._
import scalex.search.{Search, Query}
import scalex.model.{Def, Block}
import collection.mutable.WeakHashMap
import javax.servlet.http._
import com.github.ornicar.paginator.Paginator
import net.liftweb.json.JsonAST._
import net.liftweb.json.Extraction._
import net.liftweb.json.Printer._


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

  def error(msg: String): String = """{"error": "%s"}""" format msg.replace('"', "\"")

  def search(query: Query): String = Search find query match {
    case Left(msg) => error(msg)
    case Right(paginator) => toJson(Builder(query.string, paginator))
  }

  def toJson(result: Product): String =
    pretty(render(decompose(result, net.liftweb.json.DefaultFormats)))
}
