package org.scalex
package search
package query

import scala.util.{ Try, Success, Failure }

import scalaz.NonEmptyList

import model.Project

case class Area(
    name: ProjectName,
    version: List[Int]) {

  def versionString: Option[String] = version.toNel map (_.list mkString ".")

  def covers(p: Project): Boolean = covers(p.version)

  def covers(v: semverfi.Valid): Boolean =
    List(v.major, v.minor, v.patch).zipWithIndex forall {
      case (vv, pos) ⇒ compare(pos, vv)
    }

  private def compare(pos: Int, v: Int) = (version lift pos).fold(true)(v==)

  override def toString = name + (versionString ?? { "_" + _ })
}

object Area {

  def apply(str: String): Option[Area] = str match {
    case Project.nameVersionRegex(name, version) ⇒
      Area(name, version.split('.').toList.map(parseIntOption).flatten).some
    case _ ⇒ none
  }
}
