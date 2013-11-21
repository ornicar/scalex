package org.scalex
package storage

import java.io.{ File ⇒ _, _ }
import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import model.Header

private[scalex] object FileToHeader {

  def apply(file: File): Fu[Header] =
    bufferedInputStream(file) { reader ⇒
      reader.readLine.split('%').lift(1) match {
        case None      ⇒ Failure(new InvalidDatabaseException(s"No header found in $file.getName"))
        case Some(str) ⇒ HeaderFormat read str
      }
    } flatten

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
