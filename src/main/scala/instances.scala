package org.scalex

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import scalaz._

private[scalex] object instances extends instances

private[scalex] trait instances {

  implicit final class ScalexFunctor[M[_]: Functor, A](fa: M[A]) {

    def map2[N[_], B, C](f: B ⇒ C)(implicit m: A <:< N[B], f1: Functor[M], f2: Functor[N]): M[N[C]] =
      f1.map(fa) { k ⇒ f2.map(k: N[B])(f) }
  }

  implicit final class ScalexFu[A](fua: Fu[A]) {

    def void: Fu[Unit] = fua map (_ ⇒ ())

    def addEffects(fail: Exception ⇒ Unit)(succ: A ⇒ Unit): Fu[A] =
      fua ~ { _.effectFold(fail, succ) }

    def effectFold(fail: Exception ⇒ Unit, succ: A ⇒ Unit) {
      fua onComplete {
        case scala.util.Failure(e: Exception) ⇒ fail(e)
        case scala.util.Failure(e)            ⇒ throw e // Throwables
        case scala.util.Success(e)            ⇒ succ(e)
      }
    }

    def thenPp: Fu[A] = fua ~ {
      _.effectFold(
        e ⇒ println("[failure] " + e),
        a ⇒ println("[success] " + a)
      )
    }
  }

  implicit final class ScalexFuTry[A](fua: Fu[Try[A]]) {

    def flatten: Fu[A] = fua flatMap {
      case Success(a) ⇒ fuccess(a)
      case Failure(e) ⇒ Future failed e
    }
  }

  implicit final class ScalexVersion(v: semverfi.Valid) {

    def normal = semverfi.NormalVersion(v.major, v.minor, v.patch)
  }

  implicit def ScalexShowSemVersion: Show[semverfi.Valid] = Show.shows {
    version ⇒ semverfi.Show(version: semverfi.SemVersion)
  }

  implicit final class ScalexAny[A](any: A) {

    def asTry(cond: Boolean, error: ⇒ Exception): Try[A] =
      if (cond) Success(any) else Failure(error)

    def ~(sideEffect: A ⇒ Unit): A = { sideEffect(any); any }
    def pp: A = this ~ println
  }

  implicit final class ScalexOption[A](oa: Option[A]) {

    def asTry(error: ⇒ Exception): Try[A] =
      oa.fold[Try[A]](Failure(error))(Success(_))

    def ??[B: Monoid](f: A ⇒ B): B = oa.fold(∅[B])(f)
  }

  implicit final class ScalexTry[A](ta: Try[A]) {

    def fold[B](fail: Throwable ⇒ B)(succ: A ⇒ B): B = ta match {
      case Failure(e) ⇒ fail(e)
      case Success(a) ⇒ succ(a)
    }

    def failureEffect(f: PartialFunction[Throwable, Unit]): Try[A] = {
      ta recover f
      ta
    }
  }
}
