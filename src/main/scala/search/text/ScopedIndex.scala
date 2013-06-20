package org.scalex
package search
package text

private[text] final class ScopedIndex[A](indices: Map[ProjectId, Index[A]]) {

  def apply(scope: query.Scope) = indices collect {
    case (project, index) if scope(project) ⇒ index
  } toList

  def describe = indices map {
    case (id, index) ⇒ "--- %s\n%s\n---".format(id, index.describe)
  } mkString ""

  override def toString = indices map (_.toString) mkString "\n---\n"
}
