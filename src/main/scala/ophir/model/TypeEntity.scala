package ophir.model

/** A type. Note that types and templates contain the same information only for the simplest types. For example, a type
  * defines how a template's type parameters are instantiated (as in `List[Cow]`), what the template's prefix is
  * (as in `johnsFarm.Cow`), and supports compound or structural types. */
case class TypeEntity(
  /** The human-readable representation of this type. */
  val name: String
) {
  /** The human-readable representation of this type. */
  override def toString = name
}
