package ophir
package http

import org.scalatra._
import ophir.search.Search
import ophir.model.Def

class MainServlet extends ScalatraServlet {

  val limit = 10

  get("/") {
    request.getParameter("q") match {
      case null | "" => "Empty query!"
      case query => search(query)
    }
  }

  def search(query: String): String = Search find query match {
    case Left(msg) => msg
    case Right(results) => results.take(limit).toList map funToJsonObject mkString ("[", ",", "]")
  }

  def funToJsonObject(fun: Def): JsonObject = JsonObject(Map(
    "name" -> fun.name,
    "qualifiedName" -> fun.qualifiedName,
    "parent" -> JsonObject(Map(
      "name" -> fun.parent.name,
      "qualifiedName" -> fun.parent.qualifiedName
    )),
    "comment" -> JsonObject(Map(
      "html" -> fun.commentHtml,
      "text" -> fun.commentText
    )),
    "resultType" -> fun.resultType,
    "valueParams" -> fun.paramSignature,
    "signature" -> fun.signature,
    "normalizedSignature" -> fun.normalizedTypeSig
  ))

  case class JsonObject(pairs: Map[String, Any]) {

    override def toString = pairs map renderPair mkString ("{", ",", "}")

    def renderPair(pair: (String, Any)): String = pair match {
      case (k, v) => "\"%s\": \"%s\"" format (k, v)
    }
  }
}
