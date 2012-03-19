package scalex
package search

import scalaz.Validation
import scalaz.{ Success, Failure }
import scalaz.NonEmptyList

case class RawQuery(string: String, currentPage: Int, maxPerPage: Int) {

  def analyze: Validation[String, ScopedQuery] = for {
    queryAndScope ← scopeQuery(string)
    (queryString, scope) = queryAndScope
    query ← queryString match {
      case RawQuery.splitter(text, tpe) ⇒ mixQuery(text, tpe)
      case tpe if tpe contains " => "   ⇒ sigQuery(tpe)
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

  private def mixQuery(text: String, tpe: String) =
    (nameQuery(text), sigQuery(tpe)) match {
      case (Success(name), Success(sig)) ⇒ Success(MixQuery(name.tokens, sig.sig))
      case (_, sig)                     ⇒ sig
    }

  private def nameQuery(text: String) =
    tokenize(text) map NameQuery toSuccess "Empty query"

  private def sigQuery(tpe: String) =
    SigParser(tpe) map { sig ⇒ SigQuery(sig.normalize) }

  private def tokenize(text: String): Option[NonEmptyList[String]] =
    (text.toLowerCase split Array('.', ' ', '#')).toList map (_.trim) filterNot (_.isEmpty) toNel
}

object RawQuery {

  val splitter = """^([^\:]*)\:\s(.+)$""".r
  val scoper = """^(\-|\+)([a-z]+)$""".r
}
