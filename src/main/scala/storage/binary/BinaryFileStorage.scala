package org.scalex
package storage
package binary

import java.io._
import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import sbinary._, DefaultProtocol._, Operations._

import model._

private[storage] trait BinaryFileStorage extends Storage {

  import BinaryProtocol._

  def read(file: File): Future[Database] = Future {
    fromFile[Database](file)
  }

  def write(file: File, db: Database) {
    toFile(db)(file)
  }
}
