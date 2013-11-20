package org.scalex
package json

import play.api.libs.json._

import document._
import model._
import search.result.Results
import org.scalex.util.ScalexJson

object Writer extends ScalexJson {

  implicit val projectWrites: OWrites[Project] = OWrites { project =>
    import project._
    Json.obj(
      "name" -> name,
      "version" -> version.shows
    )
  }

  implicit val parentWrites: OWrites[Parent] = OWrites { parent =>
    import parent._
    Json.obj(
      "name" -> entity.name,
      "qualifiedName" -> entity.qualifiedName,
      "typeParams" -> typeParams.shows
    )
  } 

  implicit val docWrites: OWrites[Doc] = OWrites { (doc: Doc) =>
    import doc._
    Json.obj(
      "docUrl" -> "TODO",
      "name" -> name,
      "qualifiedName" -> qualifiedName,
      "typeParams" -> (doc match {
        case x: TypeParameterized => x.typeParams.shows
        case _ => ""
      }),
      "resultType" -> member.resultType,
      "valueParams" -> (doc match {
        case x: ValueParameterized => x.valueParams.shows
        case _ => ""
      }),
      "declaration" -> declaration,
      "signature" -> signature,
      "project" -> member.project,
      "deprecation" -> "TODO",
      "parent" -> member.parent,
      "comment" -> "TODO"
    )
  } dropDefaults

  implicit val resultsWrites: OWrites[Results] = OWrites { results =>
    import results._
    Json.obj(
      "query" -> query.raw,
      "projects" -> area,
      "nbResults" -> paginator.nbResults,
      "page" -> paginator.currentPage,
      "nbPages" -> paginator.nbPages,
      "milliseconds" -> duration.toMillis,
      "results" -> paginator.results.map(_.doc)
    )
  }
}
