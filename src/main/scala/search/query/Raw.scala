package ornicar.scalex
package search
package query

import scala.util.{Try, Success, Failure}
import scalaz.NonEmptyList

private[search] case class Raw(
  string: String, 
  currentPage: Int, 
  maxPerPage: Int) {

  def analyze: Try[ScopedQuery] = for {
    queryAndScope ← scopeQuery(string)
    (queryString, scope) = queryAndScope
    query ← queryString match {
      case text                         ⇒ nameQuery(text)
    }
  } yield ScopedQuery(query, scope)

  private def scopeQuery(text: String) = Success {
    if ((text contains "-") || (text contains "+")) {
      val words = text split ' ' toList
      val parsed = words.foldLeft((List[String](), QueryScope())) {
        case ((ws, scope), RawQuery.scoper(sign, pack)) ⇒ sign match {
          case "-" ⇒ (ws, scope - pack)
          case "+" ⇒ (ws, scope + pack)
          case _   ⇒ (ws, scope)
        }
        case ((ws, scope), w) ⇒ (w :: ws, scope)
      }
      (parsed._1.reverse mkString " ", parsed._2)
    }
    else (text, QueryScope())
  }

  private def nameQuery(text: String) =
    tokenize(text).toNel map NameQuery asTry badArg("The query is empty")

  private def tokenize(text: String): List[String] =
    (text.toLowerCase split Array('.', ' ', '#')).toList map (_.trim) filterNot (_.isEmpty) 
}

object RawQuery {

  val splitter = """^([^\:]*)\:\s(.+)$""".r
  val scoper = """^(\-|\+)([a-z]+)$""".r
}
