package org.scalex
package model

import scala.tools.nsc.doc.model._

case class Database(seeds: List[Seed]) {

  def projects = seeds map (_.project)

  def describe: String = seeds map (_.describe) mkString "\n\n\n"

  def merge(other: Database) = Database((seeds ++ other.seeds).distinct)
}

object Database {

  def empty = new Database(Nil)

  def merge(dbs: List[Database]): Database = 
    dbs.toNel.fold(Database.empty) { 
      _ foldLeft1 { _ merge _ }
    }
}
