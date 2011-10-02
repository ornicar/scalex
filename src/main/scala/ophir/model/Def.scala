package ophir.model

case class Def(

  /** See Entity */
    val name: String

  /** See Entity */
  , val qualifiedName: String

  /** The function host class, trait or object */
  , val parent: Parent

  /** For members representing values: the type of the value returned by this member; for members
    * representing types: the type itself. */
  , val resultType: TypeEntity

  /** The comment attached to this function, if any. */
  , val comment: String

  /** The value parameters of this method. Each parameter block of a curried method is an element of the list.
    * Each parameter block is a list of value parameters. */
  , val valueParams : List[ValueParams]

  /** The type parameters of this entity. */
  , val typeParams: List[TypeParam]

  /** The search tokens */
  , val tokens: List[String]

) extends Entity with HigherKinded {

  /** Complete signature of the function including host class and return value */
  def signature: String = List(classSignature, paramSignature, resultType) filter (_ != "") mkString " ⇒ "

  /** Signature of the host class */
  def classSignature: String = parent.toString

  /** Signature of the function parameters, not including the host class */
  def paramSignature: String = valueParams map (_.toString) mkString ""

  override def toString = qualifiedName + " " + signature
}

object Def {

  def nameToTokens(name: String): List[String] =
    name.split(Array('#', '.', ' ')).toList.distinct map (_.toLowerCase)
}

