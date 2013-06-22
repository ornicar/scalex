package org.scalex
package search
package text

import elastic.Mapping._
import play.api.libs.json._

import document._
import model.TypeParam

private[text] object Mapping {

  object fields {
    val name = "na"
    val entity = "en"
    val parent = "pa"
    val member = "me"
    val role = "ro"
    val typeParams = "tp"
    val variance = "va"
    val lo = "lo"
    val hi = "hi"
    val flags = "fl"
    val resultType = "rt"
    val defaultValue = "dv"
    val isImplicit = "ii"
  }

  import fields._

  def jsonMapping = Json.obj(
    "properties" -> Json.obj(
      name -> boost("string", 3)
    ),
    "analyzer" -> "snowball"
  )
}
