package org.scalex
package storage

import scala.util.{ Try, Success, Failure }

import model.{ Project, Header }

private[scalex] object HeaderFormat {

  private val projectSep = ";"

  private val nameVersionUrlRegex = """^([^_]+)\|(.+)\|(.*)$""".r

  def read(str: String): Try[Header] =
    str.split(projectSep).toList.foldLeft(Success(Nil): Try[List[Project]]) {
      case (Success(projects), nameVersionUrlRegex(name, version, url)) ⇒
        Project(name, version, url.some.filter(_.nonEmpty).pp) map (p ⇒ projects :+ p)
      case (_: Success[_], str) ⇒ Failure(new InvalidProjectNameException(str))
      case (e: Failure[_], _)   ⇒ e
    } map Header.apply

  def write(header: Header): String = header.projects map { p ⇒
    s"${p.name}|{$p.version}|{$p.scaladocUrl}"
  } mkString projectSep
}
