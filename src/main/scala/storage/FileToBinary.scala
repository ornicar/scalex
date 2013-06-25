package org.scalex
package storage

import java.io.{ File ⇒ _, _ }
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }
import scala.concurrent.Future

import model.Database

private[scalex] object FileToBinary {

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

  private def inputStream[A](file: File)(f: InputStream ⇒ InputStream ⇒ A): Future[A] = Future {
    val fileIn = new FileInputStream(file)
    val gzip = new GZIPInputStream(fileIn)
    val reader = new InputStreamReader(gzip)
    try {
      f(fileIn)(gzip)
    }
    finally {
      reader.close()
      gzip.close()
      fileIn.close()
    }
  }

  private def inputStreamToByteArray(is: InputStream): Array[Byte] =
    Stream.continually(is.read).takeWhile(-1 !=).map(_.toByte).toArray
}
