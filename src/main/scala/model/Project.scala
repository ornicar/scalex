package ornicar.scalex
package model

case class Project(
    name: String,
    version: String,
    templates: List[DocTemplate]) {

  def describe = name + " " + version + "\n\n" + {
    templates map (_.toString) mkString "\n"
  }
}
