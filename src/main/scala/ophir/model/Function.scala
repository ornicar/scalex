package ophir.model

case class Function(
  /** The name of the function. Note that the name does not qualify this function uniquely; use its `qualifiedName`
    * instead. */
  val name: String

  /** The qualified name of the function. This is this function's name preceded by the qualified name of the template
    * of which this function is a member. The qualified name is unique to this function. */
  , val qualifiedName: String

  /** The class, trait, object or package of which this function is a member. */
  , val parentQualifiedName: String

  /** The comment attached to this function, if any. */
  , val comment: String
) extends Model {

  def describe: String =
    (List(   qualifiedName
          , "// " + comment) mkString "\n") + "\n"

  /** The qualified name of this function. */
  override def toString = qualifiedName
}
