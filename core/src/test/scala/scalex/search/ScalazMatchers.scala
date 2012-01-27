package scalex.test

import org.specs2.matcher._
import org.specs2.execute.{ Failure ⇒ SpecFailure, Success ⇒ SpecSuccess, Result ⇒ SpecResult }
import scalaz.{ Success, Failure }
import scalaz.Validation

trait ScalazMatchers extends MatchersImplicits {

  /** success matcher for a Validation */
  def beSuccessful[E, A]: Matcher[Validation[E, A]] = (v: Validation[E, A]) ⇒ (v.fold(_ ⇒ false, _ ⇒ true), v + " successful", v + " is not successfull")

  /** failure matcher for a Validation */
  def beAFailure[E, A]: Matcher[Validation[E, A]] = (v: Validation[E, A]) ⇒ (v.fold(_ ⇒ true, _ ⇒ false), v + " is a failure", v + " is not a failure")

  /** success matcher for a Validation with a specific value */
  def succeedWith[E, A](a: ⇒ A) = validationWith[E, A](Success(a))

  def beSuccess[E, A] = new SuccessMatcher[A]

  class SuccessMatcher[A] extends Matcher[Validation[_, A]] {
    def apply[S <: Validation[_, A]](value: Expectable[S]) = {
      result(value.value.isSuccess,
        value.description + " is Success",
        value.description + " is not Success",
        value)
    }

    def like(f: PartialFunction[A, MatchResult[_]]) = this and partialMatcher(f)

    private def partialMatcher(f: PartialFunction[A, MatchResult[_]]) = new Matcher[Validation[_, A]] {
      def apply[S <: Validation[_, A]](value: Expectable[S]) = {
        val res: SpecResult = value.value match {
          case Success(t) if f.isDefinedAt(t)  ⇒ f(t).toResult
          case Success(t) if !f.isDefinedAt(t) ⇒ SpecFailure("function undefined")
          case other                           ⇒ SpecFailure("no match")
        }
        result(res.isSuccess,
          value.description + " is Success[A] and " + res.message,
          value.description + " is Success[A] but " + res.message,
          value)
      }
    }
  }

  /** failure matcher for a Validation with a specific value */
  def failWith[E, A](e: ⇒ E) = validationWith[E, A](Failure(e))

  private def validationWith[E, A](f: ⇒ Validation[E, A]): Matcher[Validation[E, A]] = (v: Validation[E, A]) ⇒ {
    val expected = f
    (expected == v, v + " is a " + expected, v + " is not a " + expected)
  }
}
