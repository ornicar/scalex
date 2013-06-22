package org.scalex
package model

import scala.util.{ Try, Success, Failure }

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

object Project extends Function2[ProjectId, SemVersion, Project] {

  private val regex = """^([^_]+)_(.+)$""".r

  def apply(name: String, version: String): Try[Project] =
    Version(version) match {
      case semverfi.Invalid(raw) ⇒ Failure(new InvalidProjectVersionException(raw))
      case v: Valid              ⇒ Success(Project(name, v))
    }

  def apply(str: String): Try[Project] = str match {
    case regex(name, version) ⇒ apply(name, version)
    case _                    ⇒ Failure(new InvalidProjectNameException(str))
  }
}
