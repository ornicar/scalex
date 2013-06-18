package ornicar.scalex
package search
package document

sealed trait Doc {

  /** unique name made of project name and qualified name */
  def id = project.fullName + ":" + qualifiedName

  /** scalex project infos */
  val project: Project

  /** scala entity qualified name */
  val qualifiedName: String

  def tokenize: List[Token] = project.tokenize ::: qualifiedName.split(".").toList
}

case class Def(
    project: Project,
    qualifiedName: String) extends Doc {

  override def toString = "def " + id
}
