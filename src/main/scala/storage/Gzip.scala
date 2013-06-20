package org.scalex
package storage

import java.io.{ File ⇒ _, _ }
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }
import scala.concurrent.Future

private[storage] trait Gzip[A] {

  def inputStream(file: File)(f: InputStream ⇒ A): Future[A] = Future {
    val fileIn = new FileInputStream(file)
    val gzip = new GZIPInputStream(fileIn)
    try {
      f(gzip)
    }
    finally {
      gzip.close()
      fileIn.close()
    }
  }

  def outputStream(file: File, a: A)(f: OutputStream ⇒ Unit) {
    file.delete()
    val fileOut = new FileOutputStream(file)
    val gzip = new GZIPOutputStream(fileOut)
    try {
      f(gzip)
    }
    finally {
      gzip.close()
      fileOut.close()
    }
  }
}
