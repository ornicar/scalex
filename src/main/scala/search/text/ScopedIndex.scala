package org.scalex
package search
package text

import scala.collection.immutable._

private[text] final class ScopedIndex[A](indices: Map[ProjectId, Index[A]]) {

  def filter(scope: query.Scope): List[(ProjectId, Index[A])] =
    indices filter {
      case (project, _) ⇒ scope(project)
    } toList

  def describe = indices map {
    case (id, index) ⇒ "--- %s\n%s\n---".format(id, index.describe)
  } mkString ""

  override def toString = indices map (_.toString) mkString "\n---\n"
}
