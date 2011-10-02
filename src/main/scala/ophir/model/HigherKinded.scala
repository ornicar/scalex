package ophir.model

/** An entity that is parameterized by types */
trait HigherKinded extends Entity {

  /** The type parameters of this entity. */
  def typeParams: List[TypeParam]

  def showTypeParams =
    if (typeParams.isEmpty) ""
    else typeParams map (_.toString) mkString ("[", ", ", "]")
}
