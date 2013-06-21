package org.scalex

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import scalaz._

private[scalex] object instances extends instances

private[scalex] trait instances {

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

  implicit final class ScalexFunctor[M[_]: Functor, A](fa: M[A]) {

    def map2[N[_], B, C](f: B ⇒ C)(implicit m: A <:< N[B], f1: Functor[M], f2: Functor[N]): M[N[C]] =
      f1.map(fa) { k ⇒ f2.map(k: N[B])(f) }
  }

  implicit final class ScalexFuture[A](fua: Future[A]) {

    def void: Future[Unit] = fua map (_ ⇒ ())

    def addEffects(failure: Exception ⇒ Unit)(success: A ⇒ Unit): Future[A] =
      fua ~ {
        _ onFailure {
          case e: Exception ⇒ failure(e)
        }
      } ~ { _ foreach success }
  }

  implicit final class ScalexAny[A](any: A) {

    def asTry(cond: Boolean, error: ⇒ Exception): Try[A] =
      if (cond) Success(any) else Failure(error)
  }

  implicit final class ScalexOption[A](oa: Option[A]) {

    def asTry(error: ⇒ Exception): Try[A] =
      oa.fold[Try[A]](Failure(error))(Success(_))

    def ??[B: Monoid](f: A ⇒ B): B = oa.fold(∅[B])(f)
  }

  implicit final class ScalexTry[A](ta: Try[A]) {

    def failureEffect(f: PartialFunction[Throwable, Unit]): Try[A] = {
      ta recover f
      ta
    }
  }
}
