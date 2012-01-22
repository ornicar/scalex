package scalex.model

/** An entity that is parameterized by types */
trait HigherKinded extends Entity {

  /** The type parameters of this entity. */
  def typeParams: List[TypeParam]

  def showTypeParams: String =
    if (typeParams.isEmpty) ""
    else typeParams map (_.toString) mkString ("[", ", ", "]")

  override def toMap = super.toMap ++ Map(
    "typeParams" -> (typeParams map (_.toMap)))
}
