package org.scalex
package storage

import sbinary._, DefaultProtocol._, Operations._

// improves sbinary Format trait
trait BinaryFormat[A] extends Format[A] {

  // scalex API
  def reader(implicit in: Input): A
  def writer(a: A)(implicit out: Output): Unit

  // sbinary API
  def reads(in: Input): A = reader(in)
  def writes(out: Output, a: A) { writer(a)(out) }

  // convenience functions for implementers
  protected def <<[B: Format](implicit in: Input): B = read[B](in)
  protected def >>[B: Format](b: B)(implicit out: Output) { write[B](out, b) }

  protected def readStr(implicit in: Input) = <<[String]
  protected def writeStr(b: String)(implicit out: Output) = >>[String](b)
}
