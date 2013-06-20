package org.scalex
package storage

import java.io.File
import model.Database
import scala.concurrent.Future

trait Storage[A] {

  def read(file: File): Future[A]

  def write(file: File, a: A): Unit
}

// object Storage extends GzipFileStorage 
object Storage extends binary.BinaryFileStorage
