package scalex
package search

import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

import model._

case class RawQuery(string: String, currentPage: Int, maxPerPage: Int) {

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  def analyze: Validation[String, Query] = failure("haha")
  //def analyze: Validation[String, Query] = string match {
    //case mixedRegex(text, tpe)      ⇒ mixedQuery(text, tpe)
    //case tpe if tpe contains " => " ⇒ typeQuery(tpe)
    //case text                       ⇒ textQuery(text)
  //}

  //private def mixedQuery(text: String, tpe: String) = success(
    //textQuery(text) + typeQuery(tpe)
  //)

  //private def textQuery(text: String) =
    //Query(TextQuery((text split ' ').toList map (_.trim)).some, none)

  //private def typeQuery(tpe: String) = for {
    //sig <- SigParser(tpe).right
  //} yield Query(none, TypeQuery(sig.normalize).some)
}

case class Query(text: Option[TextQuery], tpe: Option[TypeQuery]) {

  //def +(query: Query) = Query(query.text <+> this.text, query.tpe <+> this.tpe)
}


case class TextQuery(tokens: List[String])

case class TypeQuery(sig: NormalizedTypeSig)
