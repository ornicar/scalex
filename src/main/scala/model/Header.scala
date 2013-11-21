package org.scalex
package model

case class Header(projects: List[Project]) {

  def contains(project: Project) = projects contains project

  def merge(other: Header) = Header((projects ++ other.projects).distinct)
}

object Header {

  def merge(headers: List[Header]): Header = 
    headers.toNel.fold(Header(Nil)) { _ foldLeft1 { _ merge _ } }
}
