package ophir.model

case class Trait(

  /** See Entity */
  val name: String

  /** See Entity */
  , val qualifiedName: String

  /** The type parameters of this entity. */
  , val typeParams: List[TypeParam]

) extends Entity with Parent with HigherKinded {

  override def toString = name + showTypeParams
}
