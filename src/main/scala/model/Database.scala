package ornicar.scalex
package model

import scala.tools.nsc.doc.model._

case class Database(templates: List[DocTemplate]) {

  def describe: String = templates map (_.toString) mkString "\n"
}
