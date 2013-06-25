package org.scalex
package storage
package binary

private[scalex] object BinaryToFile {

  import Gzip.outputStream

  def apply(file: File, data: Array[Byte], header: String) {
    outputStream(file) { raw ⇒
      gzip ⇒
        raw write ("%" + header + "%\n").getBytes
        gzip write data
    }
  }
}
