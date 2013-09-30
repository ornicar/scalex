package org.scalex
package util

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.util.Timeout

private[scalex] object utilities extends utilities

private[scalex] trait utilities {

  type File = java.io.File

  implicit def execontext = scala.concurrent.ExecutionContext.Implicits.global

  type Fu[+A] = Future[A]

  def fuccess[A](a: A) = Future successful a
  def fufail[A](a: Exception): Fu[A] = Future failed a
  def fufail[A](a: String): Fu[A] = fufail(new Exception(a))
  val funit = fuccess(())

  def badArg(msg: String) = new BadArgumentException(msg)

  def parseIntOption(str: String): Option[Int] = str.parseInt.toOption

  object makeTimeout {

    implicit val short = seconds(1)
    implicit val large = seconds(5)
    implicit val veryLarge = minutes(10)
    // implicit val veryLarge = seconds(5)

    def apply(duration: FiniteDuration) = Timeout(duration)
    def seconds(s: Int): Timeout = Timeout(s.seconds)
    def minutes(m: Int): Timeout = Timeout(m.minutes)
  }
}
