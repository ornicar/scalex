package ornicar.scalex
package search

import model._

import scala.util.{ Try, Success, Failure }

final class Search(database: Database) {

  def apply(expression: String): Result = Result(Nil)
}

object Search {

  import com.typesafe.config.Config
  import scala.collection.JavaConversions._
  import index.Storage

  def apply(config: Config): Try[Search] =
    (configDbFiles(config) map Storage.read).sequence map { dbs ⇒
      new Search(Database merge dbs)
    }

  private def configDbFiles(config: Config): List[File] =
    (config getStringList "scalex.databases").toList map {
      new File(_)
      } flatMap { file ⇒
      if (file.isDirectory) file.listFiles filter isDbFile
      else List(file)
    }

  private def isDbFile(file: File) = file.getName endsWith ".scalex"
}
