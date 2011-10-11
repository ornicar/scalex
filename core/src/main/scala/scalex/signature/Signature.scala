package scalex.signature

import scalex.model._

case class Signature(

  val values: List[Either[HigherKinded, Signature]]
) {

  override def toString = values map (_.toString) mkString " => "
}

// List[A] => B => ((B, A) => B) => B
// List[A]
// B
// (B, A) => B
//   B => A => B
// B

object Signature {

  //def apply(fun: Def): Signature = {
    //val values =
      //Left(fun.parent) :: (fun.flattenedValueParams map { _ match {
        //case hk: HigherKinded
      //}})
  //}
}
