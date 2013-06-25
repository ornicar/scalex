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

private[scalex] final class Repository(config: Config) extends Actor {

  def receive = {

    // fast
    case GetProjects ⇒ projects pipeTo sender

    // slow
    case GetSeed(project) ⇒ buildFileHeaders flatMap { list ⇒
      (list find {
        case (_, header) ⇒ header contains project
      }).fold(Future successful none[Seed]) {
        case (file, _) ⇒ storage.binary.FileToBinary(file) map { bin ⇒
          storage.binary.BinaryToModel(bin).toOption flatMap (_ seedOf project)
        }
      }
    } pipeTo sender
  }

  private lazy val projects: Future[List[Project]] =
    buildFileHeaders map { list ⇒
      (Header merge list.map(_._2)).projects
    } pipeTo sender

  private lazy val buildFileHeaders: Future[List[(File, Header)]] = {
    val files = configDbFiles(config)
    println {
      ("Found %d scalex database files" format files.size) :: {
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
