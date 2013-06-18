package ornicar.scalex
package search

import document.Doc
import model.Database

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

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
  import index.Storage

  def apply(config: Config): Future[Search] = {
    val files = configDbFiles(config)
    println("Loading databases from %d files:" format files.size)
    files foreach { f ⇒ println("- " + f.getAbsolutePath) }
    Future.traverse(files)(Storage.read) map { dbs ⇒
      println("Merging databases")
      val db = Database merge dbs
      println("Loaded %d projects:".format(db.projects.size))
      db.projects foreach { p ⇒ println("- " + p.fullName) }
      println("Extracting search documents")
      val documents = document.Extractor.database(db)
      println("Indexing documents")
      val index = text.Indexer.scoped(documents)
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
    } filter (_.exists)

  private def isDbFile(file: File) = file.getName endsWith ".scalex"
}
