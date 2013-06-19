package org.scalex
package storage

import java.io._
import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import sbinary._, DefaultProtocol._, Operations._

import model._

private[storage] trait BinaryFile extends Storage {

  def read(file: File): Future[Database] = ???

  def write(file: File, db: Database) {
    ???
  }

}
