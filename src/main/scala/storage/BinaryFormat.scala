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
}
