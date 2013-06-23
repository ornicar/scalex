package org.scalex
package model

import scala.tools.nsc.doc.model._

case class Database(seeds: List[Seed]) {

  def projects = seeds map (_.project)

  def seedOf(project: Project): Option[Seed] = seeds find {
    _.project == project
  }

  def header = Header(projects)

  def describe: String = seeds map (_.describe) mkString "\n\n\n"

  def merge(other: Database) = Database((seeds ++ other.seeds).distinct)
}

object Database {

  def merge(dbs: List[Database]): Database = 
    dbs.toNel.fold(Database(Nil)) { _ foldLeft1 { _ merge _ } }
}
