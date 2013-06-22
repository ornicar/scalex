package org.scalex
package search
package text

import play.api.libs.json._

import document._
import model.TypeParam

private[text] object JsonFormat {

  val typeParamF = Json.format[model.Entity]

  // def toJson(doc: Doc): (String, JsObject) =
  //   doc.qualifiedName -> Json.obj(
  //     name -> doc.member.entity.name,
  //     member -> Json.obj(
  //       parent -> Json.obj(
  //         entity -> doc.member.parent.entity.qualifiedName,
  //         role -> doc.member.parent.role.toString,
  //         typeParams -> JsArray(doc.member.parent.typeParams map fromTypeParam)
  //       ),
  //       entity -> doc.member.entity.qualifiedName,
  //       role -> doc.member.role.toString,
  //       flags -> JsArray(doc.member.flags map JsString),
  //       resultType -> doc.member.resultType
  //     ),
  //     typeParams -> JsArray(doc match {
  //       case o: Def      ⇒ o.typeParams map fromTypeParam
  //       case o: Template ⇒ o.typeParams map fromTypeParam
  //       case _: Val      ⇒ Nil
  //     }),
  //     valueParams -> JsArray(doc match {
  //       case o: Def      ⇒ o.valueParams map2 fromValueParam
  //       case _: Val      ⇒ Nil
  //     })
  //   )

  // private def fromValueParam(o: ValueParam): JsObject = Json.obj(
  //   name -> o.name,
  //   resultType -> o.resultType,
  //   defaultValue -> o.defaultValue,
  //   isImplicit -> o.isImplicit
  // )

  // private def fromTypeParam(o: TypeParam): JsObject = Json.obj(
  //   name -> o.name,
  //   typeParams -> JsArray(o.typeParams map fromTypeParam),
  //   variance -> o.variance,
  //   lo -> o.lo,
  //   hi -> o.hi
  // )
}
