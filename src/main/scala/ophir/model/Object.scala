package ophir.model

case class Object(

  /** See Entity */
  val name: String

  /** See Entity */
  , val qualifiedName: String

) extends Entity with Parent {

  override def toString = name
}
