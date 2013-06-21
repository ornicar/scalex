package org.scalex
package search

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.pattern.{ ask, pipe }
import com.typesafe.config.Config

import makeTimeout.veryLarge
import model.Database
import util.IO._
import util.Timer._

private[search] final class SearchActor(config: Config) extends Actor {

  private var database: Database = _
  private var textualEngine: ActorRef = _

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: ActorInitializationException ⇒ Escalate
      case _: Exception                    ⇒ Restart
    }

  override def preStart {
    database = Await.result(buildDatabase, 10 minutes)
    textualEngine = context.actorOf(Props(
      new text.TextActor(database, config getConfig "text")
    ), name = "text")
  }

  def receive = {

    case expression: String ⇒ apply(expression) pipeTo sender
  }

  private def apply(expression: String): Future[Try[Results]] =
    query.Raw(expression, 1, 10).analyze match {
      case Success(query) ⇒ apply(query) map { Success(_) }
      case Failure(err)   ⇒ Future successful Failure(err)
    }

  private def apply(q: query.Query): Future[Results] = q match {
    case q: query.TextQuery ⇒ textualEngine ? q mapTo manifest[Results]
    case _                  ⇒ ???
  }

  private def buildDatabase: Future[Database] = {
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
      db
    }
  }

  private def configDbFiles(config: Config): List[File] =
    (config getStringList "databases").toList map {
      new File(_)
    } flatMap { file ⇒
      if (file.isDirectory) file.listFiles filter isDbFile
      else List(file)
    } filter (_.exists) sortBy (-_.length)

  private def isDbFile(file: File) = file.getName endsWith ".scalex"
}
