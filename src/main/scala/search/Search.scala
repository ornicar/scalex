package ornicar.scalex
package search

import model._

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

final class Search(database: Database) {

  def apply(expression: String): Try[Results] =
    apply(query.Raw(expression, 1, 10))

  def apply(raw: query.Raw): Try[Results] = raw.analyze map {
    case query.ScopedQuery(q, scope) ⇒ database.projects filter {
      project ⇒ scope(project.name)
    } flatMap (_.docs) map {
      entity ⇒ Result(entity)
    }
  }
}

object Search {

  import com.typesafe.config.Config
  import scala.collection.JavaConversions._
  import index.Storage

  def apply(config: Config): Future[Search] = {
    val files = configDbFiles(config)
    println("Loading databases from %d files:" format files.size)
    files foreach { f ⇒ println("- " + f.getAbsolutePath) }
    Future.traverse(files)(Storage.read) map { dbs ⇒
      println("Merging databases")
      val db = Database merge dbs
      val nbEntities = db.projects.map(_.countEntities).sum
      println("Loaded %d entities from %d projects:".format(nbEntities, db.projects.size))
      db.projects foreach { p ⇒ println("- " + p.fullName) }
      new Search(db)
    }
  }

  private def configDbFiles(config: Config): List[File] =
    (config getStringList "scalex.databases").toList map {
      new File(_)
    } flatMap { file ⇒
      if (file.isDirectory) file.listFiles filter isDbFile
      else List(file)
    } filter (_.exists)

  private def isDbFile(file: File) = file.getName endsWith ".scalex"
}
