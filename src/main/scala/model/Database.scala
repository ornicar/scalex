package org.scalex
package model

import scala.tools.nsc.doc.model._

case class Database(projects: List[Project]) {

  def describe: String = projects map (_.describe) mkString "\n\n\n"

  def merge(other: Database) = Database((projects ++ other.projects).distinct)
}

object Database {

  def empty = new Database(Nil)

  def merge(dbs: List[Database]): Database = 
    dbs.toNel.fold(Database.empty) { 
      _ foldLeft1 { _ merge _ }
    }
}
