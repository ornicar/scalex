package scalex
package test.search

import org.specs2.matcher._
import scalaz.{ Success, Failure }
import scalaz.Validation

trait ScalazMatchers extends MatchersImplicits {

  /** success matcher for a Validation */
  def beSuccessful[E, A]: Matcher[Validation[E, A]] = (v: Validation[E, A]) ⇒ (v.fold(_ ⇒ false, _ ⇒ true), v + " successful", v + " is not successfull")

  /** failure matcher for a Validation */
  def beAFailure[E, A]: Matcher[Validation[E, A]] = (v: Validation[E, A]) ⇒ (v.fold(_ ⇒ true, _ ⇒ false), v + " is a failure", v + " is not a failure")

  /** success matcher for a Validation with a specific value */
  def succeedWith[E, A](a: ⇒ A) = validationWith[E, A](Success(a))

  /** failure matcher for a Validation with a specific value */
  def failWith[E, A](e: ⇒ E) = validationWith[E, A](Failure(e))

  private def validationWith[E, A](f: ⇒ Validation[E, A]): Matcher[Validation[E, A]] = (v: Validation[E, A]) ⇒ {
    val expected = f
    (expected == v, v + " is a " + expected, v + " is not a " + expected)
  }
}
