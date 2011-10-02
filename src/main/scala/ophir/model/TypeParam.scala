package ophir.model

/** A type parameter to a class, trait, or method. */
case class TypeParam(

  /** See Entity */
  val name: String

  /** See Entity */
  , val qualifiedName: String

  /** The variance of this type type parameter. Valid values are "+", "-", and the empty string. */
  , val variance: String

  /** The lower bound for this type parameter, if it has been defined. */
  , val lo: Option[TypeEntity]

  /** The upper bound for this type parameter, if it has been defined. */
  , val hi: Option[TypeEntity]

  /** The type parameters of this entity. */
  , val typeParams: List[TypeParam]

) extends HigherKinded {

  override def toString =
    variance + name + (
      if (lo.isDefined) "<" + lo else ""
    ) + (
      if (hi.isDefined) ">" + lo else ""
    ) + showTypeParams
}
