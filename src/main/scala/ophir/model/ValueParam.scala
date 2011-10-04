package ophir.model

/** A value parameter to a function. */
case class ValueParam(

  /** Variable name */
  val name: String

  /** The type of this value parameter. */
  , val resultType: TypeEntity

  /** The devault value of this value parameter, if it has been defined. */
  , val defaultValue: Option[String]

  /** Whether this value parameter is implicit. */
  , val isImplicit: Boolean

) {

  /** The human-readable representation of this param. */
  override def toString = name + ": " + resultType.toString + (defaultValue match {
    case Some(dv) => " = " + dv
    case None => ""
  })
}

case class ValueParams(
  val params: List[ValueParam]
) {
  override def toString = params map (_.toString) mkString ("(", ", ", ")")
}
