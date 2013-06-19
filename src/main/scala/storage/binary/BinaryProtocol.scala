package org.scalex
package storage
package binary

import scala.collection.generic.CanBuildFrom

import sbinary._, DefaultProtocol._, Operations._

import model._

object BinaryProtocol extends DefaultProtocol with RichProtocol {

  import Sugar._

  implicit val entityF: Format[Entity] = asProduct2(Entity)(Entity.unapply(_).get)

  implicit val roleF = new BinaryFormat[Role] {
    def reader(implicit in: Input) = Role fromString <<[String]
    def writer(a: Role)(implicit out: Output) { >>(Role toString a) }
  }

  implicit val memberF: Format[Member] = asProduct6(Member)(Member.unapply(_).get)

  implicit val templateF = asProduct4(Template)(Template.unapply(_).get)

  implicit val typeParamF = new BinaryFormat[TypeParam] {

    def reader(implicit in: Input): TypeParam = TypeParam(
      name = <<[String],
      typeParams = readMany,
      variance = <<[Variance],
      lo = <<[Option[TypeEntity]],
      hi = <<[Option[TypeEntity]])

    def writer(e: TypeParam)(implicit out: Output) {
      >>(e.name)
      writeMany(e.typeParams)
      >>(e.variance)
      >>(e.lo)
      >>(e.hi)
    }
  }

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
