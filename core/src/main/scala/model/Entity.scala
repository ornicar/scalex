package scalex.model

trait Entity {
  /** The name of the entity. It does not qualify this entity uniquely; use its `qualifiedName` instead. */
  val name: String

  /** The qualified name of the function. This is this function's name preceded by the qualified name of the template
    * of which this function is a member. The qualified name is unique to this function. */
  val qualifiedName: String

  def toMap: Map[String, _] = Map(
    "name" -> name,
    "qualifiedName" -> qualifiedName)
}
