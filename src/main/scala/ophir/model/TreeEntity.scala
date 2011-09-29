package ophir.model

/** A fragment of code. */
case class TreeEntity(
  /** The human-readable representation of this abstract syntax tree. */
  val expression: String
) {

  /** The human-readable representation of this abstract syntax tree. */
  override def toString = expression
}
