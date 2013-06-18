package ornicar.scalex
package search
package text

private[text] final class ScopingIndex[A](indices: Map[ProjectName, Index[A]]) {

  // def apply(scope: query.Scope): Token ⇒ List[A] = token ⇒
  //   indices.toList flatMap {
  //     case (project, index) if scope(project) ⇒ index(token)
  //     case _                                  ⇒ Nil
  //   }

  def apply(scope: query.Scope) = indices collect {
    case (project, index) if scope(project) ⇒ index
  } toList
}
