package org.scalex
package storage
package binary

import java.io.InputStream
import scala.concurrent.Future

import model.Database

private[scalex] object FileToBinary {

  import Gzip.inputStream

  def apply(file: File): Future[Array[Byte]] =
    inputStream(file) { raw ⇒
      gzip ⇒
        try {
          while (raw.read.toChar != '\n') {}
          inputStreamToByteArray(gzip)
        }
        catch {
          case e: RuntimeException ⇒ throw new OutdatedDatabaseException(file.getName)
        }
    }

  private def inputStreamToByteArray(is: InputStream): Array[Byte] =
    Stream.continually(is.read).takeWhile(-1 !=).map(_.toByte).toArray
}
