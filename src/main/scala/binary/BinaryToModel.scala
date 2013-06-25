package org.scalex
package binary

import model.Database
import scala.util.Try

private[scalex] object BinaryToModel {

  import BinaryFormat.databaseF

  def apply(bytes: Array[Byte]): Try[Database] = Try {
    sbinary.Operations.fromByteArray(bytes)
  }
}
