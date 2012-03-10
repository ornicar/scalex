import ornicar.scalalib._

package object scalex
  extends OrnicarCommon
  with scalaz.Identitys
  with scalaz.Equals
  with scalaz.MABs
  with scalaz.Options
  with scalaz.Lists
  with scalaz.NonEmptyLists
  with scalaz.Semigroups
  with scalaz.Booleans {

  implicit def addCollectValues[A](m: Map[_, A]) = new {

    def collectValues(f: PartialFunction[A, _]) = m collect {
      case (k, v) if f isDefinedAt v => (k, f(v))
    }
  }

  implicit def addPP[A](a: A) = new {
    def pp[A] = a~println
  }
}
