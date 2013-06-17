package ornicar.scalex
package search

import model._

import scala.util.{ Try, Success, Failure }

final class Search(database: Database) {
}

object Search {

  import com.typesafe.config.Config
  import scala.collection.JavaConversions._
  import index.Storage

  def apply(config: Config): Try[Search] =
    (configDbFiles(config) map Storage.read).sequence map { dbs ⇒
      new Search(Database merge dbs)
    }

  private def configDbFiles(config: Config): List[String] =
    (config getStringList "scalex.databases").toList
}
