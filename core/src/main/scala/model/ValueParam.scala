package scalex.model

/** A value parameter to a function. */
case class ValueParam(

  /** Variable name */
  name: String

  /** The type of this value parameter. */
  , resultType: TypeEntity

  /** The devault value of this value parameter, if it has been defined. */
  , defaultValue: Option[String]

  /** Whether this value parameter is implicit. */
  , isImplicit: Boolean

) {

  /** The human-readable representation of this param. */
  override def toString = (
    if (isImplicit) "implicit " else ""
  ) + name + ": " + resultType.toString + (defaultValue match {
    case Some(dv) => " = " + dv
    case None => ""
  })
}

case class ValueParams(
  params: List[ValueParam]
) {

  def isEmpty = params.isEmpty

  override def toString =
    if (params.size > 0) params map (_.toString) mkString ("(", ", ", ")")
    else ""
}
