package org.scalex
package search
package document

sealed trait Doc extends DocImpl

case class Template(
    member: Member,
    typeParams: List[model.TypeParam]) extends Doc {

  def declaration = "%s %s.%s%s".format(
    member.role.shows,
    member.parent.entity.shortQualifiedName,
    member.entity.name,
    model.TypeParam show typeParams)
}

trait NonTemplate { self: Doc ⇒

}

case class Def(
    member: Member,
    typeParams: List[model.TypeParam],
    valueParams: List[List[model.ValueParam]]) extends Doc with NonTemplate {

  def declaration = "%s %s %s%s%s: %s".format(
    member.parent.signature,
    member.role.shows,
    member.entity.name,
    model.TypeParam show typeParams,
    model.ValueParam showCurried valueParams,
    member.resultType)
}

case class Val(
    member: Member) extends Doc with NonTemplate {

  def declaration = "%s %s %s: %s".format(
    member.parent.signature,
    member.role.shows,
    member.entity.name,
    member.resultType)
}
