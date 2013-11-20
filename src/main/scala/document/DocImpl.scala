package org.scalex
package document

private[document] trait DocImpl {

  def member: Member

  def signature: String

  def declaration: String

  // Implementation

  def id = member.projectId + ":" + qualifiedName

  def entity = member.entity

  override def toString = declaration

  def tokenize = namePile map (_.toLowerCase)

  def name = member.entity.name

  def qualifiedName = member.entity.qualifiedName

  def namePile = member.entity.namePile
}
