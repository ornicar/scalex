package ornicar

import scala.util.{ Try, Success, Failure }

package object scalex
    extends scalaz.StateFunctions // Functions related to the state monad
    with scalaz.syntax.ToTypeClassOps // syntax associated with type classes
    with scalaz.syntax.ToDataOps // syntax associated with Scalaz data structures
    with scalaz.std.AllInstances // Type class instances for the standard library types
    with scalaz.std.AllFunctions // Functions related to standard library types
    with scalaz.syntax.std.ToAllStdOps // syntax associated with standard library types
    with scalaz.IdInstances // Identity type and instances
    with scalaz.contrib.std.TryInstances
    with scalaz.contrib.std.FutureInstances {

  type File = java.io.File

  implicit final class ScalexPimpAny[A](any: A) {

    def asTry(cond: Boolean, error: ⇒ Exception): Try[A] =
      if (cond) Success(any) else Failure(error)
  }

  implicit final class ScalexPimpOption[A](oa: Option[A]) {

    def asTry(error: ⇒ Exception): Try[A] =
      oa.fold[Try[A]](Failure(error))(Success(_))

    def ??[B: scalaz.Monoid](f: A ⇒ B): B = oa.fold(∅[B])(f)
  }

  implicit final class ScalexPimpTry[A](ta: Try[A]) {

    def failureEffect(f: PartialFunction[Throwable, Unit]): Try[A] = {
      ta recover f
      ta
    }
  }

  /**
   * K combinator implementation
   * Provides oneliner side effects
   * See http://hacking-scala.posterous.com/side-effecting-without-braces
   */
  implicit def ornicarAddKcombinator[A](any: A) = new {
    def kCombinator(sideEffect: A ⇒ Unit): A = {
      sideEffect(any)
      any
    }
    def ~(sideEffect: A ⇒ Unit): A = kCombinator(sideEffect)
    def pp: A = kCombinator(println)
  }

  def badArg(msg: String) = new IllegalArgumentException(msg)
}
