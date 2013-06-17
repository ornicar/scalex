package ornicar.scalex
package model

import scala.tools.nsc.doc.model._

case class Database(projects: List[Project]) {

  def describe: String = projects map (_.describe) mkString "\n\n\n"
}
