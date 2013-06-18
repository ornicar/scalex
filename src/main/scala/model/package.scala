package ornicar.scalex

package object model {

  type TypeEntity = String
  type Flag = String
  type Variance = String
  type Expression = String
  type QualifiedName = String

  type HasTemplate = { def template: Template }

  implicit def templateRoleShow = new scalaz.Show[TemplateRole] {

    override def show(role: TemplateRole) = role match {
      case TemplateRole.CaseClass ⇒ "case class"
      case x                      ⇒ x.toString.toLowerCase
    }
  }
}
