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

  private def impl = binary.BinaryFileStorage

  def read(file: File) = impl read file

  def write(file: File, db: Database) = impl.write(file, db)
}
