package org.scalex
package storage

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.pattern.{ ask, pipe }
import com.typesafe.config.Config

import api._
import model.{ Header, Seed, Project, Database }
import util.IO._
import util.Timer._

private[scalex] final class Repository(config: Config) extends Actor with ActorLogging {

  import Repository._

  def receive = {

    // fast
    case GetProjects ⇒ {
      val replyTo = sender
      projects onComplete {
        case Success(x) ⇒ replyTo ! Projects(x)
        case Failure(e) ⇒ self ! new InvalidDatabaseException(e.toString)
      }
    }

    case e: Exception ⇒ throw e

    // slow
    case GetSeed(project) ⇒ buildFileHeaders flatMap { list ⇒
      (list find {
        case (_, header) ⇒ header contains project
      }).fold(fuccess(none[Seed])) {
        case (file, _) ⇒ storage.FileToBinary(file) flatMap { bin ⇒
          binary.BinaryToModel(bin) match {
            case Success(db) ⇒ db seedOf project match {
              case Some(seed) ⇒ fuccess(seed.some)
              case None ⇒ fufail(new InvalidDatabaseException(
                s"$file claims to contain a seed for $project"
              ))
            }
            case Failure(throwable) ⇒ fufail(new InvalidDatabaseException(
              s"Can't read project $project from $file: ${throwable.toString}"
            ))
          }
        }
      }
    } pipeTo sender
  }

  private lazy val projects: Fu[List[Project]] =
    buildFileHeaders map { list ⇒
      (Header merge list.map(_._2)).projects
    }

  private lazy val buildFileHeaders: Fu[List[(File, Header)]] = {
    val files = configDbFiles(config)
    println {
      s"Found ${files.size} scalex database files" :: {
        Nil // files map { f ⇒ "- %s (%s)".format(f.getName, ~humanReadableFileSize(f)) }
      } mkString "\n"
    }
    Future.traverse(files) { file ⇒
      (storage FileToHeader file) map (file -> _)
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

private[scalex] object Repository {

  case object GetProjects

  case class Projects(projects: List[model.Project])

  case class GetSeed(project: model.Project)
}
