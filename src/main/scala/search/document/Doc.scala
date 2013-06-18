package ornicar.scalex
package search
package document

sealed trait Doc {

  /** unique name made of project name and qualified name */
  def id = project.id + ":" + qualifiedName

  /** scalex project infos */
  def project: Project

  def entity: model.Entity

  def role: String

  def declaration: String = qualifiedName

  override def toString = "%s %s".format(role, declaration.replace("#", " "))

  def tokenize: List[Token] = project.tokenize :::
    (qualifiedName.toLowerCase split Array('.', ' ', '#')).toList map (_.trim) filterNot (_.isEmpty)

  def qualifiedName = entity.qualifiedName
}

case class Template(
    project: Project,
    memberTemplate: model.MemberTemplate) extends Doc with Member {

  def member = memberTemplate.member

  def template = memberTemplate.template

  def role = template.role.shows
}

case class Def(
    project: Project,
    member: model.Member) extends Doc with Member {

  def role = "def"
}

case class Val(
    project: Project,
    member: model.Member) extends Doc with Member {

  def role = "val"
}
