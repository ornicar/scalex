package org.scalex
package search

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import document.Doc

import model.Database

final class Search(engine: text.Api) {

  def apply(expression: String): Try[Results] =
    apply(query.Raw(expression, 1, 10))

  def apply(raw: query.Raw): Try[Results] = raw.analyze map apply

  def apply(q: query.ScopedQuery): Results = q match {
    case query.ScopedQuery(query.NameQuery(tokens), scope) ⇒
      engine search tokens.list in scope
    case _ ⇒ ???
  }
}

object Search {

  import com.typesafe.config.Config
  import scala.collection.JavaConversions._
  import util.Timer._
  import util.IO._

  def apply(config: Config): Future[Search] = {
    val files = configDbFiles(config)
    println("Found %d scalex database files" format files.size)
    files foreach { f ⇒ println("- %s (%s)".format(f.getName, ~humanReadableFileSize(f))) }
    printAndMonitorFuture("Extracting databases") {
      Future.traverse(files)(storage.Storage.read)
    } map { dbs ⇒
      val db = printAndMonitor("Merging databases") {
        Database merge dbs
      }
      println("Loaded %d projects:".format(db.projects.size))
      db.projects foreach { p ⇒ println("- " + p.fullName) }
      val documents = printAndMonitor("Extracting search documents") {
        document.Extractor.database(db)
      }
      val index = printAndMonitor("Indexing documents") {
        text.Indexer.scoped(documents)
      }
      println(index.describe)
      println("%d documents ready for search" format documents.map(_._2.size).sum)
      val textEngine = new text.Engine(index)
      val api = new text.Api(textEngine)
      new Search(api)
    }
  }

  private def configDbFiles(config: Config): List[File] =
    (config getStringList "scalex.databases").toList map {
      new File(_)
    } flatMap { file ⇒
      if (file.isDirectory) file.listFiles filter isDbFile
      else List(file)
    } filter (_.exists) sortBy (-_.length)

  private def isDbFile(file: File) = file.getName endsWith ".scalex"
}
