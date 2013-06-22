package org.scalex
package model

import scala.util.{ Try, Success, Failure }

import semverfi.{ Version, Valid }

case class Project(name: ProjectId, version: Valid) {

  def id: ProjectId = name + "_" + version.shows

  def fullName = name + " " + version.shows

  // def versionMatch(v: Valid) = (version, v) match {

  //   case (a, b) ⇒ a == b
  // }

  def semVersion: SemVersion = version

  override def toString = id

  override def equals(other: Any) = other match {
    case p: Project ⇒ name == p.name && version.shows == p.version.shows
    case _          ⇒ false
  }

  lazy val tokenize: List[Token] = name :: version.shows :: Nil
}

object Project extends Function2[ProjectId, Valid, Project] {

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
