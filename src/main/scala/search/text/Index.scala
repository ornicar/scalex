package org.scalex
package search
package text

import elastic.Mapping._
import play.api.libs.functional.syntax._
import play.api.libs.json._

import document._
import model.json._
import model.{ TypeParam, ValueParam, Project, Role, Entity, Block, Comment }

private[text] object Index extends org.scalex.util.ScalexJson {

  object fields {
    val name = "na"
    val entity = "en"
    val parent = "pa"
    val member = "me"
    val comment = "co"
    val role = "ro"
    val typeParams = "tp"
    val valueParams = "vp"
    val variance = "va"
    val lo = "lo"
    val hi = "hi"
    val flags = "fl"
    val resultType = "rt"
    val defaultValue = "dv"
    val isImplicit = "ii"

    val memberEntity = member + "." + entity
  }

  def mapping = Json.obj(
    "properties" -> Json.obj(
      f.name -> boost("string", 10),
      f.member -> Json.obj(
        "properties" -> Json.obj(
          f.entity -> boost("string", 2)
        )
      )
    ),
    "analyzer" -> "snowball"
  )

  private def f = fields
}
