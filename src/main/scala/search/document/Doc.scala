package ornicar.scalex
package search
package document

sealed trait Doc {

  /** scala entity qualified name */
  val qualifiedName: String
}

case class Def(
    qualifiedName: String) extends Doc {

  override def toString = "def " + qualifiedName
}
