package ornicar

import scala.util.{ Try, Success, Failure }
import scalaz._

package object scalex
    extends StateFunctions // Functions related to the state monad
    with syntax.ToTypeClassOps // syntax associated with type classes
    with syntax.ToDataOps // syntax associated with Scalaz data structures
    with std.AllInstances // Type class instances for the standard library types
    with std.AllFunctions // Functions related to standard library types
    with syntax.std.ToAllStdOps // syntax associated with standard library types
    with IdInstances // Identity type and instances
    with contrib.std.TryInstances
    with contrib.std.FutureInstances {

  type File = java.io.File

  implicit def execontext = scala.concurrent.ExecutionContext.Implicits.global

  implicit final class ScalexFunctor[M[_]: Functor, A](fa: M[A]) {

    def map2[N[_], B, C](f: B ⇒ C)(implicit m: A <:< N[B], f1: Functor[M], f2: Functor[N]): M[N[C]] =
      f1.map(fa) { k ⇒ f2.map(k: N[B])(f) }
  }

  implicit final class ScalexPimpAny[A](any: A) {

    def asTry(cond: Boolean, error: ⇒ Exception): Try[A] =
      if (cond) Success(any) else Failure(error)
  }

  implicit final class ScalexPimpOption[A](oa: Option[A]) {

    def asTry(error: ⇒ Exception): Try[A] =
      oa.fold[Try[A]](Failure(error))(Success(_))

    def ??[B: Monoid](f: A ⇒ B): B = oa.fold(∅[B])(f)
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
