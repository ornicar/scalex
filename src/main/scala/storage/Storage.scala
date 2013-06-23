package org.scalex
package storage

import scala.concurrent.Future

import model.{ Header, Database }

trait Storage[H, D] {

  def header(file: File): Future[H] 

  def read(file: File): Future[D]

  def write(file: File, a: D): Unit
}

object Storage extends Storage[Header, Database] {

  private def impl = binary.BinaryFileStorage

  def header(file: File) = impl header file

  def read(file: File) = impl read file

  def write(file: File, db: Database) = impl.write(file, db)
}
