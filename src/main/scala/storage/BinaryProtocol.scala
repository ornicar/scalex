package org.scalex
package storage

import scala.collection.generic.CanBuildFrom

import sbinary._, DefaultProtocol._, Operations._

import model._

object BinaryProtocol extends DefaultProtocol {

  private def <<[B: Format](implicit in: Input): B = read[B](in)
  private def >>[B: Format](b: B)(implicit out: Output) { write[B](out, b) }

  private def readStr(implicit in: Input) = <<[String]
  private def writeStr(b: String)(implicit out: Output) = >>[String](b)

  implicit val entityF: Format[Entity] = asProduct2(Entity)(Entity.unapply(_).get)

  implicit val roleF = new BinaryFormat[Role] {
    def reader(implicit in: Input) = Role fromString <<[String]
    def writer(a: Role)(implicit out: Output) { >>(Role toString a) }
  }

  implicit val memberF: Format[Member] = asProduct6(Member)(Member.unapply(_).get)

  implicit val templateF = asProduct4(Template)(Template.unapply(_).get)

  case class Ent(a: Int, b: List[Ent])

  implicit val EntFormat = new BinaryFormat[Ent] {
    def reader(implicit in: Input): Ent = readEnt
    def writer(e: Ent)(implicit out: Output) {

    }
  }

  def readEnt(implicit in: Input): Ent = Ent(a = <<[Int], b = readEntList)

  def readEntList(implicit in: Input): List[Ent] = 
    readSeq[List, Ent] { implicit in ⇒ readEnt }

  def readSeq[CC[X] <: Traversable[X], T](reader: Input ⇒ T)(
    implicit binT: Format[T],
    cbf: CanBuildFrom[Nothing, T, CC[T]],
    in: Input): CC[T] = {
    val size = <<[Int]
    val builder = cbf() ~ { _ sizeHint size }
    var i = 0
    while (i < size) {
      builder += reader(in)
      i += 1
    }
    builder.result
  }

  // def readE(implicit in: Input) = E(<<[Int], readEs(in))

  // def EsF: Format[TypeParam] = new LengthEncoded

  // def readEs(implicit in: Input) = 

  // implicit lazy val EF = lazyFormat(
  //   asProduct2(E)(E.unapply(_).get)
  // )

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
