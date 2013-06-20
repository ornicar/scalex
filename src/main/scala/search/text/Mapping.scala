package org.scalex
package search
package text

import play.api.libs.json._

import elastic.Mapping._

private[text] object Mapping {

  object fields {
    val name = "na"
    val project = "pro"
  }
  import fields._

  def jsonMapping = Json.obj(
    "properties" -> Json.toJson(List(
      boost(name, "string", 3)
    ).toMap),
    "analyzer" -> "snowball"
  )

  // def from(team: TeamModel): JsObject = Json.obj(
  //   name -> team.name,
  //   description -> team.description,
  //   location -> ~team.location,
  //   nbMembers -> team.nbMembers
  // )
}
