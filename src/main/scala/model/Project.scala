package org.scalex
package model

import semverfi.{ Version, SemVersion, Valid, Show }

case class Project(name: ProjectId, version: SemVersion) {

  def id: ProjectId = name + "_" + showVersion

  def fullName = name + " " + showVersion

  def showVersion = Show(version)

  override def toString = id

  override def equals(other: Any) = other match {
    case p: Project ⇒ name == p.name && showVersion == p.showVersion
    case _          ⇒ false
  }

  lazy val tokenize: List[Token] = name :: showVersion :: Nil
}

object Project {

  private val regex = """^([^_]+)_(.+)$""".r

  def apply(name: String, version: String): Option[Project] =
    Version(version) match {
      case semverfi.Invalid(raw) ⇒ {
        println("Invalid project version: " + raw)
        none
      }
      case v: Valid ⇒ Project(name, v).some
    }

  def apply(str: String): Option[Project] = str match {
    case regex(name, version) ⇒ apply(name, version)
    case _ ⇒ {
      println("Invalid project name: " + str)
      none
    }
  }
}
