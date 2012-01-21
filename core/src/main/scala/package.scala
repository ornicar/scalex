package object scalex
  extends scalaz.Identitys
  with scalaz.Equals
  with scalaz.MABs
  with scalaz.MAs
  with scalaz.Options
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
}
