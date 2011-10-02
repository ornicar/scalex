package ophir.model

case class Parent(

  /** See Entity */
  val name: String

  /** See Entity */
  , val qualifiedName: String

  /** The type parameters of this entity. */
  , val typeParams: List[TypeParam]

  , val isObject: Boolean

) extends Entity with HigherKinded {

  override def toString = name + showTypeParams
}

object Parent {

  def makeObject(name: String, qualifiedName: String) =
    Parent(name, qualifiedName, Nil, true)

  def makeTrait(name: String, qualifiedName: String, typeParams: List[TypeParam]) =
    Parent(name, qualifiedName, typeParams, false)
}
