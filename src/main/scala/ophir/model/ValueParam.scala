package ophir.model

/** A value parameter to a function. */
case class ValueParam(
  /** The name of the param instead. */
  val name: String

  /** The type of this value parameter. */
  , val resultType: TypeEntity

  /** The devault value of this value parameter, if it has been defined. */
  , val defaultValue: Option[TreeEntity]

  /** Whether this value parameter is implicit. */
  , val isImplicit: Boolean
) {
  /** The human-readable representation of this param. */
  override def toString = name + ": " + resultType.toString
}
