package org.scalex
package search
package text

import play.api.libs.functional.syntax._
import play.api.libs.json._

import document._
import model.json._
import model.{ TypeParam, ValueParam, Project, Role, Entity, Block, Comment }

private[search] object ElasticToDocument extends org.scalex.util.ScalexJson {

  private val f = Index.fields

  def apply(project: Project, id: String, json: JsValue): Option[Doc] = {

    def readTypeParam(js: JsValue): Option[TypeParam] = for {
      tp ← js.asOpt[JsObject]
      name ← tp str f.name
      typeParams = (tp arr f.typeParams) ?? {
        _.value.toList.map(readTypeParam).flatten
      }
      variance ← tp str f.variance
      lo = tp str f.lo
      hi = tp str f.hi
    } yield TypeParam(name, typeParams, variance, lo, hi)

    def readTypeParams(js: JsValue): List[TypeParam] =
      (js arr f.typeParams) ?? { _.value.toList.map(readTypeParam).flatten }

    def readValueParam(js: JsValue): Option[ValueParam] = for {
      tp ← js.asOpt[JsObject]
      name ← tp str f.name
      resultType ← tp str f.resultType
      defaultValue = tp str f.defaultValue
      isImplicit = (tp boolean f.isImplicit) | false
    } yield ValueParam(name, resultType, defaultValue, isImplicit)

    json.asOpt[JsObject] flatMap { doc ⇒
      (for {
        m ← doc obj f.member
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
      } yield Member(project, parent, comment, entity, role, flags, resultType)) map { member ⇒
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
