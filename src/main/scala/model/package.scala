package ornicar.scalex

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
}
