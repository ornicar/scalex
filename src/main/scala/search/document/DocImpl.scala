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

  def tokenize = namePile map (_.toLowerCase)

  def qualifiedName = member.entity.qualifiedName

  def namePile = member.entity.namePile
}
