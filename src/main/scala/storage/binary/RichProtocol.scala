package org.scalex
package storage
package binary

import scala.collection.generic.CanBuildFrom

import sbinary._, DefaultProtocol._, Operations._

private[binary] trait RichProtocol {

  object Sugar {

    def <<[B: Format](implicit in: Input): B = read[B](in)
    def >>[B: Format](b: B)(implicit out: Output) { write[B](out, b) }

    def readStr(implicit in: Input) = <<[String]
    def writeStr(b: String)(implicit out: Output) = >>[String](b)
  }

  // improves sbinary Format trait
  trait BinaryFormat[A] extends Format[A] {

    // scalex API
    def reader(implicit in: Input): A
    def writer(a: A)(implicit out: Output): Unit

    // free functions
    def readMany(implicit in: Input): List[A] =
      SeqFormat.reader[List, A] { implicit in ⇒ reader }
    def writeMany(as: List[A])(implicit out: Output) {
      SeqFormat.writer[List, A](as) { implicit out ⇒ a ⇒ writer(a) }
    }

    // sbinary API
    def reads(in: Input): A = reader(in)
    def writes(out: Output, a: A) { writer(a)(out) }
  }

  private object SeqFormat {

    def reader[CC[X] <: Traversable[X], T](
      readOne: Input ⇒ T)(
        implicit cbf: CanBuildFrom[Nothing, T, CC[T]],
        in: Input): CC[T] = {
      val size = read[Int](in)
      val builder = cbf()
      builder sizeHint size
      var i = 0
      while (i < size) {
        builder += readOne(in)
        i += 1
      }
      builder.result
    }

    def writer[CC[X] <: Traversable[X], T](
      ts: CC[T])(
        writer: Output ⇒ T ⇒ Unit)(
          implicit cbf: CanBuildFrom[Nothing, T, CC[T]],
          out: Output) {
      val len = ts.size
      write(out, len)
      ts foreach (t ⇒ writer(out)(t))
    }
  }
}
