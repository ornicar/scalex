package ophir.model

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
  , comment: String

  /** The value parameters of this method. Each parameter block of a curried method is an element of the list.
    * Each parameter block is a list of value parameters. */
  , valueParams : List[ValueParams]

  /** The type parameters of this entity. */
  , typeParams: List[TypeParam]

  /** The search text tokens */
  , tokens: List[String]

  /** The search type tokens */
  //, typeTokens: List[String]

) extends Entity with HigherKinded {

  /** Complete signature of the function including host class and return value */
  def signature: String = List(classSignature, paramSignature, resultType) filter (_ != "") mkString " ⇒ "

  /** Signature of the host class */
  def classSignature: String = parent.toString

  /** Signature of the function parameters, not including the host class */
  def paramSignature: String = valueParams map (_.toString) mkString ""

  /** All value params flattened, ignoring curried methods separations */
  def flatValueParams: List[ValueParam] =
    valueParams.foldLeft(List[ValueParam]())((a, b) => a ::: b.params)

  /** Non implicit value params */
  def flatNonImplicitValueParams: List[ValueParam] =
    flatValueParams filter (!_.isImplicit)


  override def toString = (
    qualifiedName + showTypeParams + ": " + signature
  ) + "\n" + typeSig + "\n" + typeSigIndex

  def typeSig = TypeSig(parent.toTypeEntity :: (flatNonImplicitValueParams map (_.resultType)) ::: List(resultType))

  val typeSigIndex: String = typeSig.toIndex
}

object Def {

  def nameToTokens(name: String): List[String] =
    name.split(Array('#', '.', ' ')).toList.distinct map (_.toLowerCase)

  def typeToTokens(name: String): List[String] =
    name.split(Array('#', '.', ' ')).toList.distinct map (_.toLowerCase)
}
