package org.scalex
package storage

import sbinary._, DefaultProtocol._, Operations._

import model._

object BinaryProtocol extends DefaultProtocol {

  implicit val entityF: Format[Entity] = asProduct2(Entity)(Entity.unapply(_).get)

  implicit val roleF = new BinaryFormat[Role] {
    def reader(implicit in: Input) = Role fromString <<[String]
    def writer(a: Role)(implicit out: Output) { >>(Role toString a) }
  }

  implicit val memberF: Format[Member] = asProduct6(Member)(Member.unapply(_).get)

  implicit val templateF = asProduct4(Template)(Template.unapply(_).get)

  // implicit val typeParamsF = format[List[TypeParam]]
  // case class TypeParams(unwrap: List[TypeParam])

  // implicit lazy val typeParamF = lazyFormat(asUnion[TypeParam](
  //   asProduct5(TypeParam)(TypeParam.unapply(_).get),
  //   wrap[List[TypeParam], TypeParams](Typeparams, _.unwrap)
  // ))

  // implicit val docTemplateFormat = new BinaryFormat[DocTemplate] {

  //   def reader(implicit in: Input) = DocTemplate(
  //     member = <<[Member],
  //     template = <<[Template],
  //     typeParams = <<[List[TypeParam]],
  //     valueParams = <<[List[List[ValueParam]]],
  //     parentTypes = <<[List[QualifiedName]],
  //     members = <<[List[Member]],
  //     templates = <<[List[DocTemplate]],
  //     methods = <<[List[Def]],
  //     values = <<[List[Val]],
  //     abstractTypes = <<[List[AbstractType]],
  //     aliasTypes = <<[List[AliasType]],
  //     primaryConstructor = <<[Option[Constructor]],
  //     constructors = <<[List[Constructor]])

  //   def writer(a: DocTemplate)(implicit out: Output) {
  //   }
  // }

  // private def 
}
