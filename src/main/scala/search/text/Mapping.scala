package org.scalex
package search
package text

import elastic.Mapping._
import play.api.libs.json._

import document._

private[text] object Mapping {

  object fields {
    val project = "pro"
    val name = "na"
  }

  import fields._

  def jsonMapping = Json.obj(
    "properties" -> Json.toJson(List(
      // don't analyse the project field
      // it makes filters containgin hyphens fail
      // http://stackoverflow.com/questions/11566838/elastic-search-hyphen-issue-with-term-filter
      field(project, "string", false),
      boost(name, "string", 3)
    ).toMap),
    "analyzer" -> "snowball"
  )

  def from(projectId: ProjectId, doc: Doc): (String, JsObject) =
    doc.qualifiedName -> Json.obj(
      project -> projectId,
      name -> doc.member.entity.name)
}
