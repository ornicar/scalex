package org.scalex
package storage

import java.io.File
import model.Database
import scala.concurrent.Future

trait Storage {

  def read(file: File): Future[Database]

  def write(file: File, db: Database): Unit
}

object Storage extends Storage with GzipFile
