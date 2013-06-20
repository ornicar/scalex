package org.scalex
package elastic

import org.joda.time.format.{ DateTimeFormat, DateTimeFormatter }
import play.api.libs.json._

private[scalex] object Mapping {

  object Date {

    val format = "YYYY-MM-dd HH:mm:ss"

    val formatter: DateTimeFormatter = DateTimeFormat forPattern format
  }

  def field(name: String, typ: String, analyzed: Boolean = false, attrs: JsObject = Json.obj()) =
    name -> (Json.obj(
      "type" -> typ,
      "index" -> analyzed.fold("analyzed", "not_analyzed")
    ) ++ attrs)

  def boost(name: String, typ: String, b: Int = 1, attrs: JsObject = Json.obj()) =
    field(name, typ, true, Json.obj("boost" -> b) ++ attrs)

  def obj(name: String, properties: JsObject) =
    name -> Json.obj("type" -> "object", "properties" -> properties)
}
