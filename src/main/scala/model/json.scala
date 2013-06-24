package org.scalex
package model

import play.api.libs.functional.syntax._
import play.api.libs.json._

private[scalex] object json {

  implicit val blockFormat = (
    (__ \ "html").format[String] and
    (__ \ "txt").format[String]
  )(Block.apply, unlift(Block.unapply))

  implicit val commentFormat = (
    (__ \ "body").format[Block] and
    (__ \ "summary").format[Block] and
    (__ \ "see").format[List[Block]] and
    (__ \ "result").format[Option[Block]] and
    (__ \ "throws").format[Map[String, Block]] and
    (__ \ "valueParams").format[Map[String, Block]] and
    (__ \ "typeParams").format[Map[String, Block]] and
    (__ \ "version").format[Option[Block]] and
    (__ \ "since").format[Option[Block]] and
    (__ \ "todo").format[List[Block]] and
    (__ \ "deprecated").format[Option[Block]] and
    (__ \ "note").format[List[Block]] and
    (__ \ "example").format[List[Block]] and
    (__ \ "constructor").format[Option[Block]]
  )(Comment.apply, unlift(Comment.unapply))
}
