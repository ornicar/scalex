package org.scalex
package search
package query

import scala.util.{ Try, Success, Failure }

import semverfi.SemVersion

import model.Project

case class Area(name: ProjectName, version: Option[SemVersion]) {

  override def toString = name + "_" + version.shows
}

object Area {

  def apply(str: String): Option[Area] =
    str.trim.some.filter(_.nonEmpty) map { s ⇒
      Project(s) match {
        case Success(Project(name, version)) ⇒ Area(name, version.some)
        case _                               ⇒ Area(s, none)
      }
    }
}
