package scalex.model

import com.novus.salat.annotations._

case class Def(

  /** See Entity */
    name: String

  /** See Entity */
  , qualifiedName: String

  /** The function host class, trait or object */
  , parent: Parent

  /** For members representing values: the type of the value returned by this member; for members
    * representing types: the type itself. */
  , resultType: TypeEntity

  /** The comment attached to this function, if any. */
  , commentHtml: String

  /** The comment attached to this function, if any, translated to raw text. */
  , commentText: String

  /** The value parameters of this method. Each parameter block of a curried method is an element of the list.
    * Each parameter block is a list of value parameters. */
  , valueParams : List[ValueParams]

  /** The type parameters of this entity. */
  , typeParams: List[TypeParam]

  /** The search text tokens */
  , tokens: List[String]

  /** The search signature tokens */
  , sigTokens: List[String]

) extends Entity with HigherKinded {

  /** Complete signature of the function including host class and return value */
  def signature: String = List(classSignature, paramSignature, resultType) filter (_ != "") mkString " => "

  /** Signature of the host class */
  def classSignature: String = parent.toString

  /** Signature of the function parameters, not including the host class */
  def paramSignature: String = valueParams map (_.toString) mkString ""

  override def toString = (
    qualifiedName + showTypeParams + ": " + signature
  )

}

object Def {

  def nameToTokens(name: String): List[String] =
    name.split(Array('#', '.', ' ')).toList.distinct map (_.toLowerCase.trim)
}
