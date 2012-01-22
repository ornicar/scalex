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
  , comment: Option[Comment]

  /** The value parameters of this method. Each parameter block of a curried method is an element of the list.
    * Each parameter block is a list of value parameters. */
  , valueParams : List[ValueParams]

  /** The type parameters of this entity. */
  , typeParams: List[TypeParam]

  /** The search text tokens */
  , tokens: List[String]

  /** The search signature tokens */
  , sigTokens: List[String]

  /** The package containing the def */
  , pack: String

  /** Some deprecation message if this function is deprecated, or none otherwise. */
  , deprecation: Option[Block]

) extends Entity with HigherKinded {

  /** Complete signature of the function including host class and return value */
  def signature: String = List(classSignature, paramSignature, resultType) filter (_ != "") mkString " => "

  /** Signature of the host class */
  def classSignature: String = parent.toString

  /** Signature of the function parameters, not including the host class */
  def paramSignature: String = valueParams map (_.toString) mkString ""

  def id = hashCode toString

  override def toString = (
    qualifiedName + showTypeParams + ": " + signature
  )
}

object Def {

  def nameToTokens(name: String): List[String] =
    name.split(Array('#', '.', ' ')).toList.distinct map (_.toLowerCase.trim)
}
