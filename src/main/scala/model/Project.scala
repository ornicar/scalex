package org.scalex
package model

case class Project(
    name: String,
    version: String,
    root: DocTemplate) {

  def describe = fullName + "\n\n" + {
    root.templates map (_.toString) mkString "\n"
  }

  def fullName = name + " " + version

  override def toString = fullName

  override def equals(other: Any) = other match {
    case p: Project ⇒ name == p.name && version == p.version
    case _       ⇒ false
  }
}
