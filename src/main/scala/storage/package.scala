package org.scalex

package object storage {

  final class OutdatedDatabaseException(msg: String) extends Exception(msg)
}
