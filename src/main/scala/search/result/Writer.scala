package org.scalex
package search
package result

import play.api.libs.json._

import document._
import model._
import org.scalex.util.ScalexJson

object Writer extends ScalexJson {

  implicit val projectWrites: OWrites[Project] = OWrites { project =>
    import project._
    Json.obj(
      "name" -> name,
      "version" -> version.shows,
      "docUrl" -> scaladocUrl
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

  implicit val blockWrites: OWrites[Block] = OWrites { block =>
    Json.obj(
      "txt" -> block.txt,
      "html" -> block.html
    )
  } 

  implicit val commentWrites: OWrites[Comment] = OWrites { comment =>
    import comment._
    Json.obj(
      "short" -> summary,
      "body" -> body,
      "see" -> see,
      "result" -> result,
      "throws" -> throws,
      "valueParams" -> valueParams,
      "typeParams" -> typeParams,
      "version" -> version,
      "since" -> since,
      "todo" -> todo,
      "deprecated" -> deprecated,
      "note" -> note,
      "example" -> example,
      "constructor" -> constructor
    )
  } 

  implicit val docWrites: OWrites[Doc] = OWrites { (doc: Doc) =>
    import doc._
    Json.obj(
      "docUrl" -> scaladocUrl,
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
      // TODO get global project
      "project" -> member.project,
      "deprecation" -> "TODO",
      "parent" -> member.parent,
      "comment" -> member.comment.map(Json.toJson(_))
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
