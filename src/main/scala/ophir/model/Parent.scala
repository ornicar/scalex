package ophir.model

case class Parent(

  /** See Entity */
  name: String

  /** See Entity */
  , qualifiedName: String

  /** The type parameters of this entity. */
  , typeParams: List[TypeParam]

  , isObject: Boolean

) extends HigherKinded {

  override def toString = name + showTypeParams

  def toTypeEntity: TypeEntity = Class(name, true, typeParams map (_.toTypeEntity))
}

object Parent {

  def makeObject(name: String, qualifiedName: String) =
    Parent(name, qualifiedName, Nil, true)

  def makeTrait(name: String, qualifiedName: String, typeParams: List[TypeParam]) =
    Parent(name, qualifiedName, typeParams, false)
}
