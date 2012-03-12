package scalex
package search

import scalaz.Semigroup
import scalaz.Validation
import scalaz.{ Success, Failure }
import scalaz.NonEmptyList

import model.NormalizedTypeSig

case class RawQuery(string: String, currentPage: Int, maxPerPage: Int) {

  val splitter = """^([^\:]*)\:\s(.+)$""".r

  def analyze: Validation[String, Query] = string match {
    case splitter(text, tpe)        ⇒ mixQuery(text, tpe)
    case tpe if tpe contains " => " ⇒ sigQuery(tpe)
    case text                       ⇒ textQuery(text)
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

case class MixQuery(tokens: NonEmptyList[String], sig: NormalizedTypeSig) extends Query {
  override def toString = "%s : %s".format(tokens.list mkString " + ", sig)
}

case class TextQuery(tokens: NonEmptyList[String]) extends Query {
  override def toString = tokens.list mkString " + "
}

case class SigQuery(sig: NormalizedTypeSig) extends Query {
  override def toString = sig.toString
}
