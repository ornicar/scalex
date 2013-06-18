package ornicar.scalex
package search
package document

private[document] trait DocImpl {

  def project: Project

  def parent: Parent

  def member: model.Member

  def role: model.Role

  def declaration: String

  // Implementation

  def id = project.id + ":" + qualifiedName

  def entity = member.entity

  override def toString = declaration

  def tokenize: List[Token] = project.tokenize :::
    (qualifiedName.toLowerCase split Array('.', ' ', '#')).toList map (_.trim) filterNot (_.isEmpty)

  def qualifiedName = entity.qualifiedName
}
