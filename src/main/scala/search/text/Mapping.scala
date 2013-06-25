package org.scalex
package search
package text

import elastic.Mapping._
import play.api.libs.functional.syntax._
import play.api.libs.json._

import document._
import model.{ TypeParam, ValueParam, Project, Role, Entity, Block, Comment }
import model.json._

private[text] object Mapping extends org.scalex.util.ScalexJson {

  object f {
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

  def jsonMapping = Json.obj(
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

  def write(doc: Doc): (String, JsObject) = {

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

  def read(projectName: ProjectName, id: String, json: JsValue): Option[Doc] = {

    def readTypeParam(js: JsValue): Option[TypeParam] =
      js.asOpt[JsObject] flatMap { tp ⇒
        for {
          name ← tp str f.name
          typeParams = (tp arr f.typeParams) ?? {
            _.value.toList.map(readTypeParam).flatten
          }
          variance ← tp str f.variance
          lo = tp str f.lo
          hi = tp str f.hi
        } yield TypeParam(name, typeParams, variance, lo, hi)
      }
    def readTypeParams(js: JsValue): List[TypeParam] =
      (js arr f.typeParams) ?? { _.value.toList.map(readTypeParam).flatten }

    def readValueParam(js: JsValue): Option[ValueParam] =
      js.asOpt[JsObject] flatMap { tp ⇒
        for {
          name ← tp str f.name
          resultType ← tp str f.resultType
          defaultValue = tp str f.defaultValue
          isImplicit = (tp boolean f.isImplicit) | false
        } yield ValueParam(name, resultType, defaultValue, isImplicit)
      }

    json.asOpt[JsObject] flatMap { doc ⇒
      doc obj f.member flatMap { m ⇒
        for {
          project ← Project(projectName).toOption
          parent ← m obj f.parent flatMap { parent ⇒
            for {
              entity ← parent str f.entity map Entity
              role ← parent str f.role map Role.fromName
              typeParams = readTypeParams(parent)
            } yield Parent(entity, role, typeParams)
          }
          comment = m.get[Comment](f.comment)
          entity = Entity(id)
          role ← m str f.role map Role.fromName
          flags = (m arr f.flags) ?? { ~_.asOpt[List[String]] }
          resultType ← m str f.resultType
        } yield Member(project, parent, comment, entity, role, flags, resultType)
      } map { member ⇒
        member.role match {
          case Role.Def ⇒ Def(
            member,
            readTypeParams(doc),
            (doc arr f.valueParams) ?? {
              _.value.toList map {
                _.asOpt[JsArray] ?? {
                  _.value.toList.map(readValueParam).flatten
                }
              }
            })
          case Role.Val | Role.LazyVal | Role.Var ⇒ Val(member)
          case _                                  ⇒ Template(member, readTypeParams(doc))
        }
      }
    }
  }
}
