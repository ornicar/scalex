package org.scalex
package storage

import java.io.{ File ⇒ _, _ }
import scala.concurrent.Future

import model.Header

private[scalex] object FileToHeader {

  def apply(file: File): Fu[Header] =
    bufferedInputStream(file) { reader ⇒
      Header(~reader.readLine.split('%').lift(1))
    }

  private def bufferedInputStream[H](file: File)(f: BufferedReader ⇒ H): Fu[H] = Future {
    val fileIn = new FileInputStream(file)
    val decoder = new InputStreamReader(fileIn, encoding)
    val buffered = new BufferedReader(decoder)
    try {
      f(buffered)
    }
    finally {
      buffered.close()
      decoder.close()
      fileIn.close()
    }
  }
}
