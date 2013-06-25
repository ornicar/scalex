package org.scalex
package storage

import java.io.{ File ⇒ _, _ }
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }
import scala.concurrent.Future

private[storage] object Gzip {

  def inputStream[A](file: File)(f: InputStream ⇒ InputStream ⇒ A): Future[A] = Future {
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

  def outputStream[A](file: File)(f: OutputStream ⇒ OutputStream ⇒ Unit) {
    file.delete()
    val fileOut = new FileOutputStream(file)
    val gzip = new GZIPOutputStream(fileOut)
    try {
      f(fileOut)(gzip)
    }
    finally {
      gzip.close()
      fileOut.close()
    }
  }
}
