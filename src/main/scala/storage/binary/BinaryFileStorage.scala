package org.scalex
package storage
package binary

import scala.concurrent.Future

import model.{ Database, Header }

private[storage] object BinaryFileStorage extends Storage[Header, Database] with Gzip[Database] {

  import BinaryProtocol._

  def header(file: File): Future[Header] = 
    bufferedInputStream(file) map Header.apply

  def read(file: File): Future[Database] =
    inputStream(file) { gzip ⇒
      try {
        sbinary.Operations.read[Database](gzip)
      }
      catch {
        case e: RuntimeException ⇒ throw new OutdatedDatabaseException(file.getName)
      }
    }

  def write(file: File, db: Database) {
    outputStream(file, db) { raw ⇒
      gzip ⇒
        raw write (db.header.toString + "\n").getBytes
        gzip write sbinary.Operations.toByteArray(db)
    }
  }
}
