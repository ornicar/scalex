package ornicar.scalex
package search
package document

case class Project(name: String, version: String) {

  def fullName = name + "_" + version

  def tokenize: List[Token] = name :: version :: Nil
}
