package org.scalex
package storage
package binary

import scala.concurrent.Future

import model.Database

private[storage] trait BinaryFileStorage extends Storage[Database] with Gzip[Database] {

  import BinaryProtocol._

  def read(file: File): Future[Database] =
    inputStream(file) { gzip ⇒
      sbinary.Operations.read[Database](gzip)
    }

  def write(file: File, db: Database) {
    outputStream(file, db) { gzip ⇒
      gzip write sbinary.Operations.toByteArray(db)
    }
  }
}
