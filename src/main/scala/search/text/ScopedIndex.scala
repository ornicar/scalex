package ornicar.scalex
package search
package text

private[text] final class ScopedIndex[A](indices: Map[ProjectId, Index[A]]) {

  def apply(scope: query.Scope) = indices collect {
    case (project, index) if scope(project) ⇒ index
  } toList

  override def toString = indices map (_.toString) mkString "\n---\n"
}
