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

  def covers(p: Project): Boolean = name == p.name && covers(p.version)

  def covers(v: semverfi.Valid): Boolean =
    List(v.major, v.minor, v.patch).zipWithIndex forall {
      case (vv, pos) ⇒ compare(pos, vv)
    }

  private def compare(pos: Int, v: Int) = (version lift pos).fold(true)(v==)

  override def toString = name + (versionString ?? { "_" + _ })
}

object Area {

  val nameVersionRegex = """^([^_]+)_(.+)$""".r

  def apply(str: String): Area = str match {

    case nameVersionRegex(name, version) ⇒
      Area(name, version.split('.').toList.map(parseIntOption).flatten)

    case name ⇒ Area(name, Nil)
  }
}
