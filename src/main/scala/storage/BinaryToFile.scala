package org.scalex
package storage

import java.io.{ File ⇒ _, _ }
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }

private[scalex] object BinaryToFile {

  def apply(file: File, data: Array[Byte], header: String) {
    outputStream(file) { raw ⇒
      gzip ⇒
        raw write ("%" + header + "%\n").getBytes
        gzip write data
    }
  }

  private def outputStream[A](file: File)(f: OutputStream ⇒ OutputStream ⇒ Unit) {
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
