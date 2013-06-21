package org.scalex
package model

case class Seed(
  project: Project,
  root: DocTemplate) {

  def describe = project.fullName + "\n\n" + {
    root.templates map (_.toString) mkString "\n"
  }

  def fullName = project.fullName

  override def toString = project.id

  override def equals(other: Any) = other match {
    case s: Seed ⇒ project == s.project
    case _       ⇒ false
  }
}
