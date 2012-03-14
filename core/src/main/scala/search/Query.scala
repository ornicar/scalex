package scalex
package search

import scalaz.Validation
import scalaz.{ Success, Failure }
import scalaz.NonEmptyList

import model.NormalizedTypeSig

case class RawQuery(string: String, currentPage: Int, maxPerPage: Int) {

  val splitter = """^([^\:]*)\:\s(.+)$""".r
  val scoper = """^(\-|\+)([a-z]+)$""".r

  def analyze: Validation[String, ScopedQuery] = for {
    queryAndScope ← scopeQuery(string)
    (queryString, scope) = queryAndScope
    query ← queryString match {
      case splitter(text, tpe)        ⇒ mixQuery(text, tpe)
      case tpe if tpe contains " => " ⇒ sigQuery(tpe)
      case text                       ⇒ textQuery(text)
    }
  } yield ScopedQuery(query, scope)

  private def scopeQuery(text: String) = Success {
    if ((text contains "-") || (text contains "+")) {
      val words = text split ' ' toList
      val parsed = words.foldLeft((List[String](), Scope())) {
        case ((ws, scope), scoper(sign, pack)) ⇒ sign match {
          case "-" ⇒ (ws, scope - pack)
          case "+" ⇒ (ws, scope + pack)
          case _   ⇒ (ws, scope)
        }
        case ((ws, scope), w) ⇒ (w :: ws, scope)
      }
      (parsed._1.reverse mkString " ", parsed._2)
    }
    else (text, Scope())
  }

  private def mixQuery(text: String, tpe: String) =
    (textQuery(text), sigQuery(tpe)) match {
      case (Success(txt), Success(sig)) ⇒ Success(MixQuery(txt.tokens, sig.sig))
      case (_, sig)                     ⇒ sig
    }

  private def textQuery(text: String) =
    tokenize(text) map TextQuery toSuccess "Empty query"

  private def sigQuery(tpe: String) =
    SigParser(tpe) map { sig ⇒ SigQuery(sig.normalize) }

  private def tokenize(text: String): Option[NonEmptyList[String]] =
    (text.toLowerCase split ' ').toList map (_.trim) filterNot (_.isEmpty) toNel
}

trait Query

case class Scope(
    only: List[String] = Nil,
    without: List[String] = Nil) {

  def +(pack: String) = copy(only = only :+ pack)

  def -(pack: String) = copy(without = without :+ pack)

  override def toString =
    (only map ("+" + _)) ::: (without map ("-" + _)) mkString " "
}

case class ScopedQuery(query: Query, scope: Scope)

case class MixQuery(
    tokens: NonEmptyList[String],
    sig: NormalizedTypeSig) extends Query {
  override def toString = "%s : %s".format(tokens.list mkString " and ", sig)
}

case class TextQuery(tokens: NonEmptyList[String]) extends Query {
  override def toString = tokens.list mkString " and "
}

case class SigQuery(sig: NormalizedTypeSig) extends Query {
  override def toString = sig.toString
}
