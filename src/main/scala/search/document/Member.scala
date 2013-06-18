package ornicar.scalex
package search.document

private[document] trait Member { self: Doc ⇒

  def member: model.Member

  def entity = member.entity
}
