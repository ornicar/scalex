package ornicar.scalex
package model

case class Project(
    name: String,
    version: String,
    templates: List[DocTemplate]) {

  def countEntities = templates.map(_.countEntities).sum

  def describe = fullName + "\n\n" + {
    templates map (_.toString) mkString "\n"
  }

  def fullName = name + " " + version

  override def equals(other: Any) = other match {
    case p: Project ⇒ name == p.name && version == p.version
    case _       ⇒ false
  }
}
