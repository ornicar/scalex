package org.scalex
package util

import scala.math

private[scalex] object IO {

  def humanReadableFileSize(file: File): Option[String] = 
    (file.exists option file.length) map humanReadableByteCount

  def humanReadableByteCount(bytes: Long): String = {
    val unit = 1024
    if (bytes < unit) bytes + " B"
    else {
      val exp = (math.log(bytes) / math.log(unit)).toInt
      val pre = "KMGTPE".charAt(exp - 1)
      "%.1f %sB".format(bytes / math.pow(unit, exp), pre)
    }
  }
}
