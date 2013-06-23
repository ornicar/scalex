package org.scalex
package model

case class Header(projects: List[Project]) {

  override def toString = projects mkString ";"

  def contains(project: Project) = projects contains project

  def merge(other: Header) = Header((projects ++ other.projects).distinct)
}

object Header {

  def apply(str: String): Header = Header {
    str.split(';').toList.map(Project.apply).map(_.toOption).flatten 
  }

  def merge(headers: List[Header]): Header = 
    headers.toNel.fold(Header(Nil)) { _ foldLeft1 { _ merge _ } }
}
