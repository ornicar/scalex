package scalex
package search

import scalaz.Semigroup
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }
import scalaz.NonEmptyList

import model.NormalizedTypeSig

case class RawQuery(string: String, currentPage: Int, maxPerPage: Int) {

  private val splitter = """^([^\:]*)\:\s(.+)$""".r

  def analyze: Validation[String, Query] = string match {
    case splitter(text, tpe)        ⇒ mixQuery(text, tpe)
    case tpe if tpe contains " => " ⇒ sigQuery(tpe)
    case text                       ⇒ textQuery(text)
  }

  private def mixQuery(text: String, tpe: String) = for {
    text ← textQuery(text)
    sig ← sigQuery(tpe)
  } yield MixQuery(text.tokens, sig.sig)

  private def textQuery(text: String) =
    tokenize(text) map TextQuery toSuccess "Empty query"

  private def sigQuery(tpe: String) =
    SigParser(tpe) map { sig ⇒ SigQuery(sig.normalize) }

  private def tokenize(text: String): Option[NonEmptyList[String]] =
    (text.toLowerCase split ' ').toList map (_.trim) filterNot (_.isEmpty) toNel
}

trait Query

case class MixQuery(tokens: NonEmptyList[String], sig: NormalizedTypeSig) extends Query

case class TextQuery(tokens: NonEmptyList[String]) extends Query

case class SigQuery(sig: NormalizedTypeSig) extends Query
