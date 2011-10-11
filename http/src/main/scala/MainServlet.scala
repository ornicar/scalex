package scalex
package http

import org.scalatra._
import scalex.search.Search
import scalex.model.Def

class MainServlet extends ScalatraServlet {

  val limit = 20

  get("/") {
    contentType = "application/json"

    val response = request.getParameter("q") match {
      case null | "" => "Empty query!"
      case query => search(query, page)
    }

    // handle jsonp
    request.getParameter("callback") match {
      case null | "" => response
      case callback => "%s(%s)" format (callback, response)
    }
  }

  notFound {
    <h1>Not found. Bummer.</h1>
  }

  def page: Int = request.getParameter("page") match {
    case null | "" | "0" => 1
    case spage => spage.toInt
  }

  def search(query: String, page: Int): String = Search find query match {
    case Left(msg) => JsonObject(Map("error" -> msg)).toString
    case Right(results) => makeResult(results.take(limit).toList, page).toString
  }

  def makeResult(funs: List[Def], page: Int): JsonObject = JsonObject(Map(
    "results" -> (funs map funToJsonObject),
    "nb" -> funs.size,
    "page" -> page
  ))

  def funToJsonObject(fun: Def): JsonObject = JsonObject(Map(
    "name" -> fun.name,
    "qualifiedName" -> fun.qualifiedName,
    "parent" -> JsonObject(Map(
      "name" -> fun.parent.name,
      "qualifiedName" -> fun.parent.qualifiedName,
      "typeParams" -> fun.parent.showTypeParams
    )),
    "comment" -> JsonObject(Map(
      //"html" -> fun.commentHtml,
      "text" -> fun.commentText
    )),
    "typeParams" -> fun.showTypeParams,
    "resultType" -> fun.resultType,
    "valueParams" -> fun.paramSignature,
    "signature" -> fun.signature
  ))

  case class JsonObject(pairs: Map[String, Any]) {

    override def toString = pairs map renderPair mkString ("{", ", ", "}")

    def renderPair(pair: (String, Any)): String = pair match {
      case (k, v) => "\"%s\": %s" format (k, renderValue(v))
    }

    def renderValue(value: Any): String = value match {
      case v: JsonObject => v.toString
      case v: List[_] => v mkString ("[", ", ", "]")
      case v => quote(v.toString)
    }

    /**
    * Quote a string according to "JSON rules".
    */
    def quote(s: String) = {
      val charCount = s.codePointCount(0, s.length)
      "\"" + 0.to(charCount - 1).map { idx =>
        s.codePointAt(s.offsetByCodePoints(0, idx)) match {
          case 0x0d => "\\r"
          case 0x0a => "\\n"
          case 0x09 => "\\t"
          case 0x22 => "\\\""
          case 0x5c => "\\\\"
          case 0x2f => "\\/" // to avoid sending "</"
          case c => quotedChar(c)
        }
      }.mkString("") + "\""
    }

    private[this] def quotedChar(codePoint: Int) = {
      codePoint match {
        case c if c > 0xffff =>
          val chars = Character.toChars(c)
          "\\u%04x\\u%04x".format(chars(0).toInt, chars(1).toInt)
        case c if c > 0x7e => "\\u%04x".format(c.toInt)
        case c => c.toChar
      }
    }
  }
}
