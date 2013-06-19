package org.scalex
package search
package document

private[document] trait DocImpl {

  def member: Member

  def declaration: String

  // Implementation

  def id = member.project.id + ":" + qualifiedName

  def entity = member.entity

  override def toString = declaration

  def tokenize: List[Token] = member.project.tokenize :::
    (qualifiedName.toLowerCase split Array('.', ' ', '#')).toList map (_.trim) filterNot (_.isEmpty)

  def qualifiedName = member.entity.qualifiedName
}
