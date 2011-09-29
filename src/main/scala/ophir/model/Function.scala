package ophir.model

case class Function(
  /** The name of the function. Note that the name does not qualify this function uniquely; use its `qualifiedName`
    * instead. */
  val name: String

  /** For members representing values: the type of the value returned by this member; for members
    * representing types: the type itself. */
  , val resultType: TypeEntity

  /** The qualified name of the function. This is this function's name preceded by the qualified name of the template
    * of which this function is a member. The qualified name is unique to this function. */
  , val qualifiedName: String

  /** The class, trait, object or package of which this function is a member. */
  , val parentQualifiedName: String

  /** The comment attached to this function, if any. */
  , val comment: String

  /** The value parameters of this method. Each parameter block of a curried method is an element of the list.
    * Each parameter block is a list of value parameters. */
  , val valueParams : List[List[ValueParam]]
) extends Model {

  def describe: String =
    (List(   qualifiedName
          , "// " + comment) mkString "\n") + "\n"

  /** The qualified name of this function. */
  override def toString = qualifiedName
}
