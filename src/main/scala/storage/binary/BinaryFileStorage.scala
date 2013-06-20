package org.scalex
package storage
package binary

import scala.concurrent.Future

import model.Database

private[storage] object BinaryFileStorage extends Storage[Database] with Gzip[Database] {

  import BinaryProtocol._

  def read(file: File): Future[Database] =
    inputStream(file) { gzip ⇒
      try {
        sbinary.Operations.read[Database](gzip)
      }
      catch {
        case e: RuntimeException ⇒
          throw new OutdatedDatabaseException("The database %s is too old and must be rebuilded" format file.getName)
      }
    }

  def write(file: File, db: Database) {
    outputStream(file, db) { gzip ⇒
      gzip write sbinary.Operations.toByteArray(db)
    }
  }
}
