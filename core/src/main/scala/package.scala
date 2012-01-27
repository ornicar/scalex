package object scalex
  extends scalaz.Identitys
  with scalaz.Equals
  with scalaz.MABs
  with scalaz.Options
  with scalaz.Lists
  with scalaz.Semigroups
  with scalaz.Booleans {

  /**
   * K combinator implementation
   * Provides oneliner side effects
   * See http://hacking-scala.posterous.com/side-effecting-without-braces
   */
  implicit def addKcombinator[A](any: A) = new {

    def kCombinator(sideEffect: A => Unit): A = {
      sideEffect(any)
      any
    }
    def ~(sideEffect: A => Unit): A = kCombinator(sideEffect)
  }

  implicit def addCollectValues[A](m: Map[_, A]) = new {

    def collectValues(f: PartialFunction[A, _]) = m collect {
      case (k, v) if f isDefinedAt v => (k, f(v))
    }
  }
}
