package scalex
package search

import scalaz.Semigroup
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }
import scalaz.NonEmptyList

import model._

case class RawQuery(string: String, currentPage: Int, maxPerPage: Int) {

  implicit def QuerySemigroup: Semigroup[Query] = semigroup(_ merge _)

  private val splitter = """^([^\:]*)\:\s(.+)$""".r

  def analyze: Validation[String, Query] = string match {
    case splitter(text, tpe)      ⇒ mixedQuery(text, tpe)
    case tpe if tpe contains " => " ⇒ typeQuery(tpe)
    case text                       ⇒ textQuery(text)
  }

  private def mixedQuery(text: String, tpe: String) =
    textQuery(text) >>*<< typeQuery(tpe)

  private def textQuery(text: String) =
    tokenize(text) map { tokens =>
      Query(TextQuery(tokens).some, none)
    } toSuccess "Empty query"

  private def typeQuery(tpe: String) = SigParser(tpe) map { sig ⇒
    Query(none, TypeQuery(sig.normalize).some)
  }

  private def tokenize(text: String): Option[NonEmptyList[String]] =
    (text split ' ').toList map (_.trim) filterNot (_.isEmpty) toNel
}

case class Query(text: Option[TextQuery], tpe: Option[TypeQuery]) {

  def merge(query: Query) = Query(query.text <+> this.text, query.tpe <+> this.tpe)
}

case class TextQuery(tokens: NonEmptyList[String])

case class TypeQuery(sig: NormalizedTypeSig)
