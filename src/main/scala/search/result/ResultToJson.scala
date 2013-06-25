package org.scalex
package search
package result

import play.api.libs.json._
import model.json._
import document._

object ResultToJson {

  def apply(results: Results): JsObject = {
    import results._
    Json.obj(
      "query" -> query.raw,
      "nbResults" -> paginator.nbResults,
      "page" -> paginator.currentPage,
      "nbPages" -> paginator.nbPages,
      "milliseconds" -> duration.toMillis,
      "results" -> paginator.results.map(apply)
    )
  }

  def apply(result: Result): JsObject = {
    import result._
    Json.obj(
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
}
