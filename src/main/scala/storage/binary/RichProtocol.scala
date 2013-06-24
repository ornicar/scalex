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

  def asProduct14[S, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14](
    apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) ⇒ S)(
      unapply: S ⇒ Product14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14])(implicit bin1: Format[T1],
        bin2: Format[T2],
        bin3: Format[T3],
        bin4: Format[T4],
        bin5: Format[T5],
        bin6: Format[T6],
        bin7: Format[T7],
        bin8: Format[T8],
        bin9: Format[T9],
        bin10: Format[T10],
        bin11: Format[T11],
        bin12: Format[T12],
        bin13: Format[T13],
        bin14: Format[T14]) = new Format[S] {
    def reads(in: Input): S = apply(
      read[T1](in),
      read[T2](in),
      read[T3](in),
      read[T4](in),
      read[T5](in),
      read[T6](in),
      read[T7](in),
      read[T8](in),
      read[T9](in),
      read[T10](in),
      read[T11](in),
      read[T12](in),
      read[T13](in),
      read[T14](in))
    def writes(out: Output, s: S) = {
      val product = unapply(s)
      write(out, product._1)
      write(out, product._2)
      write(out, product._3)
      write(out, product._4)
      write(out, product._5)
      write(out, product._6)
      write(out, product._7)
      write(out, product._8)
      write(out, product._9)
      write(out, product._10)
      write(out, product._11)
      write(out, product._12)
      write(out, product._13)
      write(out, product._14)
    }
  }
}
