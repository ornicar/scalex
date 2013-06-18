package ornicar.scalex
package search
package document

sealed trait Doc extends DocImpl

case class Template(
    project: Project,
    parent: Parent,
    member: model.Member,
    role: model.Role,
    typeParams: List[model.TypeParam]) extends Doc {

  def declaration = "%s %s%s".format(
    role.shows,
    entity.qualifiedName,
    model.TypeParam show typeParams)
}

trait NonTemplate { self: Doc ⇒

}

case class Def(
    project: Project,
    parent: Parent,
    member: model.Member,
    role: model.Role,
    typeParams: List[model.TypeParam],
    valueParams: List[List[model.ValueParam]]) extends Doc with NonTemplate {

  def declaration = "%s %s %s%s%s: %s".format(
    parent.signature,
    role.shows,
    entity.name,
    model.TypeParam show typeParams,
    model.ValueParam showCurried valueParams,
    member.resultType)
}

case class Val(
    project: Project,
    parent: Parent,
    member: model.Member,
    role: model.Role) extends Doc with NonTemplate {

  def declaration = "%s %s %s: %s".format(
    parent.signature,
    role.shows,
    entity.name,
    member.resultType)
}
