package org.scalex
package search
package text

import elastic.Mapping._
import play.api.libs.functional.syntax._
import play.api.libs.json._

import document._
import model.{ TypeParam, ValueParam, Project, Role, Entity, Block, Comment }
import model.json._

private[text] object DocumentToElastic extends org.scalex.util.ScalexJson {

  private val f = Index.fields

  def apply(doc: Doc): (String, JsObject) = {

    def writeValueParam(o: ValueParam): JsObject = Json.obj(
      f.name -> o.name,
      f.resultType -> o.resultType,
      f.defaultValue -> o.defaultValue,
      f.isImplicit -> o.isImplicit)

    def writeTypeParam(o: TypeParam): JsObject = Json.obj(
      f.name -> o.name,
      f.typeParams -> JsArray(o.typeParams map writeTypeParam),
      f.variance -> o.variance,
      f.lo -> o.lo,
      f.hi -> o.hi)

    doc.qualifiedName -> Json.obj(
      f.name -> doc.member.entity.name,
      f.member -> Json.obj(
        f.parent -> Json.obj(
          f.entity -> doc.member.parent.entity.qualifiedName,
          f.role -> doc.member.parent.role.name,
          f.typeParams -> JsArray(doc.member.parent.typeParams map writeTypeParam)
        ),
        f.comment -> Json.toJson(doc.member.comment),
        f.entity -> doc.member.entity.qualifiedName,
        f.role -> doc.member.role.name,
        f.flags -> JsArray(doc.member.flags map JsString),
        f.resultType -> doc.member.resultType
      ),
      f.typeParams -> JsArray(doc match {
        case o: Def      ⇒ o.typeParams map writeTypeParam
        case o: Template ⇒ o.typeParams map writeTypeParam
        case _: Val      ⇒ Nil
      }),
      f.valueParams -> JsArray(doc match {
        case o: Def ⇒ o.valueParams map { vps ⇒
          JsArray(vps map writeValueParam)
        }
        case _ ⇒ Nil
      })
    )
  }

}
