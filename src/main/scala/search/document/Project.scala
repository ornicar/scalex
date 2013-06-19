package org.scalex
package search
package document

case class Project(name: String, version: String) {

  def id: ProjectId = name + "_" + version

  def fullName = name + " " + version

  override def toString = fullName

  lazy val tokenize: List[Token] = name :: version :: Nil
}
