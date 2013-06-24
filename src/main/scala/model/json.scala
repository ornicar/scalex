package org.scalex
package model

import play.api.libs.functional.syntax._
import play.api.libs.json._

import org.scalex.util.ScalexJson._

private[scalex] object json {

  implicit val blockFormat = (
    (__ \ "html").format[String] and
    (__ \ "txt").format[String]
  )(Block.apply, unlift(Block.unapply))

  implicit val commentFormat = (
    (__ \ "body").format[Block] and
    (__ \ "summary").format[Block] and
    (__ \ "see").format[List[Block]].default and
    (__ \ "result").format[Option[Block]].default and
    (__ \ "throws").format[Map[String, Block]].default and
    (__ \ "valueParams").format[Map[String, Block]].default and
    (__ \ "typeParams").format[Map[String, Block]].default and
    (__ \ "version").format[Option[Block]].default and
    (__ \ "since").format[Option[Block]].default and
    (__ \ "todo").format[List[Block]].default and
    (__ \ "deprecated").format[Option[Block]].default and
    (__ \ "note").format[List[Block]].default and
    (__ \ "example").format[List[Block]].default and
    (__ \ "constructor").format[Option[Block]].default
  )(Comment.apply, unlift(Comment.unapply))
}
