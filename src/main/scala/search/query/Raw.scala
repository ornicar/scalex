package org.scalex
package search
package query

import scala.util.{ Try, Success, Failure }

import scalaz.{ \/, -\/, \/- }

private[search] case class Raw(
    string: String,
    currentPage: Int,
    maxPerPage: Int) {

  def analyze: String \/ Query = \/- {
    val (queryString, scope) = scopeQuery(string)
    text.Query(
      string,
      tokens = tokenize(queryString),
      scope = scope,
      pagination = Pagination(currentPage, maxPerPage))
  }

  private def scopeQuery(t: String) =
    if ((t contains "-") || (t contains "+")) {
      val words = t split ' ' toList
      val parsed = words.foldLeft((List[String](), Scope())) {
        case ((ws, scope), Raw.scoper(sign, pack)) ⇒ sign match {
          case "-" ⇒ (ws, scope - pack)
          case "+" ⇒ (ws, scope + pack)
          case _   ⇒ (ws, scope)
        }
        case ((ws, scope), w) ⇒ (w :: ws, scope)
      }
      (parsed._1.reverse mkString " ", parsed._2)
    }
    else (t, Scope())

  private def tokenize(t: String): List[String] =
    (t.toLowerCase split Array('.', ' ', '#')).toList map (_.trim) filterNot (_.isEmpty)
}

object Raw {

  val splitter = """^([^\:]*)\:\s(.+)$""".r
  val scoper = """^(\-|\+)([a-z]+)$""".r
}
