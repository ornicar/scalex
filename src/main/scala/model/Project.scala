package ornicar.scalex
package model

case class Project(
    name: String,
    version: String,
    templates: List[DocTemplate]) {

  def describe = name + " " + version + "\n\n" + {
    templates map (_.toString) mkString "\n"
  }

  override def equals(other: Any) = other match {
    case p: Project ⇒ name == p.name && version == p.version
    case _       ⇒ false
  }
}
