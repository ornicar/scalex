package org.scalex
package binary

import scala.collection.generic.CanBuildFrom

import sbinary._, DefaultProtocol._, Operations._
import semverfi.{ Valid, Version }

import model._

private[binary] object BinaryFormat extends DefaultProtocol with RichProtocol {

  import Sugar._

  implicit val entityF = wrap[Entity, String](_.qualifiedName, Entity.apply)

  implicit val roleF = new BinaryFormat[Role] {
    def reader(implicit in: Input) = Role fromName <<[String]
    def writer(a: Role)(implicit out: Output) { >>(Role toName a) }
  }

  implicit val blockF: Format[Block] = asProduct2(Block)(Block.unapply(_).get)

  implicit val commentF: Format[Comment] = asProduct14(Comment)(Comment.unapply(_).get)

  implicit val memberF: Format[Member] = asProduct7(Member)(Member.unapply(_).get)

  implicit val templateF = asProduct4(Template)(Template.unapply(_).get)

  implicit val valueParamF = asProduct4(ValueParam)(ValueParam.unapply(_).get)

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

  implicit val defF = asProduct3(Def)(Def.unapply(_).get)

  implicit val constructorF = asProduct2(Constructor)(Constructor.unapply(_).get)

  implicit val abstractTypeF = asProduct4(AbstractType)(AbstractType.unapply(_).get)

  implicit val docTemplateFormat = new BinaryFormat[DocTemplate] {

    def reader(implicit in: Input) = DocTemplate(
      member = <<[Member],
      template = <<[Template],
      typeParams = <<[List[TypeParam]],
      valueParams = <<[List[List[ValueParam]]],
      parentTypes = <<[List[QualifiedName]],
      members = <<[List[Member]],
      templates = readMany,
      methods = <<[List[Def]],
      values = <<[List[Member]],
      abstractTypes = <<[List[AbstractType]],
      aliasTypes = <<[List[TypeEntity]],
      primaryConstructor = <<[Option[Constructor]],
      constructors = <<[List[Constructor]])

    def writer(a: DocTemplate)(implicit out: Output) {
      >>(a.member)
      >>(a.template)
      >>(a.typeParams)
      >>(a.valueParams)
      >>(a.parentTypes)
      >>(a.members)
      writeMany(a.templates)
      >>(a.methods)
      >>(a.values)
      >>(a.abstractTypes)
      >>(a.aliasTypes)
      >>(a.primaryConstructor)
      >>(a.constructors)
    }
  }

  implicit val versionF = new BinaryFormat[Valid] {
    def reader(implicit in: Input) = Version(<<[String]).opt err "Invalid binary version"
    def writer(v: Valid)(implicit out: Output) { >>(v.shows) }
  }

  implicit val projectF = asProduct3(Project)(Project.unapply(_).get)

  implicit val seedF = asProduct2(Seed)(Seed.unapply(_).get)

  implicit val databaseF = wrap[Database, List[Seed]](_.seeds, Database.apply)
}
