package scalex.model

/** A type parameter to a class, trait, or method. */
case class TypeParam(

  /** See Entity */
  name: String

  /** See Entity */
  , qualifiedName: String

  /** The variance of this type type parameter. Valid values are "+", "-", and the empty string. */
  , variance: String

  /** The lower bound for this type parameter, if it has been defined. */
  , lo: Option[TypeEntity]

  /** The upper bound for this type parameter, if it has been defined. */
  , hi: Option[TypeEntity]

  /** The type parameters of this entity. */
  , typeParams: List[TypeParam]

) extends HigherKinded {

  override def toString =
    variance + name + (
      lo some ("<" + _) none ""
    ) + (
      hi some (">" + _) none ""
    ) + showTypeParams

  def toTypeEntity: TypeEntity = Class(name, name != qualifiedName, typeParams map (_.toTypeEntity))
}

object TypeParam {

  def apply(name: String, typeParams: List[TypeParam] = Nil): TypeParam =
    TypeParam(name, "", "", None, None, typeParams)

}
