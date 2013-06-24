package org.scalex
package search
package result

import play.api.libs.json._

import document._
import model.instances._
import model.json._

case class Result(doc: Doc, score: Score) {

  override def toString =
    """%s
%s
%s""".format(doc.name, doc, doc.member.comment ?? (_.summary.txt))

  def toJson: JsObject = Json.obj(
    "docUrl" -> "TODO",
    "name" -> doc.name,
    "qualifiedName" -> doc.qualifiedName,
    "typeParams" -> (doc match {
      case o: Def      ⇒ o.typeParams.shows
      case o: Template ⇒ o.typeParams.shows
      case _: Val      ⇒ ""
    }),
    "valueParams" -> (doc match {
      case o: Def ⇒ o.valueParams.shows
      case _      ⇒ ""
    }),
    "resultType" -> doc.member.resultType,
    "declaration" -> doc.declaration,
    "signature" -> doc.signature,
    "package" -> doc.member.project.toString,
    "deprecation" -> Json.obj(
      "html" -> "???",
      "txt" -> "???"
    ),
    "parent" -> Json.obj(
      "name" -> doc.member.parent.entity.name,
      "qualifiedName" -> doc.member.parent.entity.qualifiedName,
      "typeParams" -> doc.member.parent.typeParams.shows
    ),
    "comment" -> doc.member.comment.map(Json.toJson(_))
  )
}
