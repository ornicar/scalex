package org.scalex

import scala._ // import sorter hack

package object model {

  type TypeEntity = String
  type Flag = String
  type Variance = String
  type Expression = String
  type QualifiedName = String

  implicit def roleShow = new scalaz.Show[Role] {

    override def show(role: Role) = role match {
      case Role.CaseClass    ⇒ "case class"
      case Role.LazyVal      ⇒ "lazy val"
      case Role.AliasType    ⇒ "type alias"
      case Role.AbstractType ⇒ "abstract type"
      case x                 ⇒ x.toString.toLowerCase
    }
  }

  private def showTypeParam(tp: TypeParam): String =
    tp.variance + tp.name + (tp.hi ?? ("<:" + _)) + (tp.lo ?? (">:" + _)) + tp.typeParams.shows

  implicit def typeParamsShow = new scalaz.Show[List[TypeParam]] {

    override def show(tps: List[TypeParam]) =
      if (tps.isEmpty) ""
      else tps map showTypeParam mkString ("[", ", ", "]")
  }

  implicit def typeParamShow = new scalaz.Show[TypeParam] {

    override def show(tp: TypeParam) = showTypeParam(tp)
  }
}
