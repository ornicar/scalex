package org.scalex
package storage

import java.io.File
import scala.concurrent.Future

import model.Database

trait Storage[A] {

  def read(file: File): Future[A]

  def write(file: File, a: A): Unit
}

object Storage extends Storage[Database] {

  import util.IO._
  import util.Timer._

  private def impl = binary.BinaryFileStorage

  def read(file: File) = {
    printAndMonitorFuture("<< %s (%s)".format(
      file.getName,
      humanReadableFileSize(file) | "?"
    )) {
      impl.read(file)
    }
  }

  def write(file: File, db: Database) {
    printAndMonitor(">> " + file.getName) {
      impl.write(file, db)
    }
  }
}
