package scalex.model

case class Comment(

  /** The main body of the comment that describes what the entity does and is.  */
  body: Block

  /** A shorter version of the body. Usually, this is the first sentence of the body. */
  , short: Block

  /** A list of authors. The empty list is used when no author is defined. */
  , authors: List[Block] = Nil

  /** A list of other resources to see, including links to other entities or to external documentation. The empty list
    * is used when no other resource is mentionned. */
  , see: List[Block] = Nil

  /** A description of the result of the entity. Typically, this provides additional information on the domain of the
    * result, contractual post-conditions, etc. */
  , result: Option[Block] = None

  /** A map of exceptions that the entity can throw when accessed, and a description of what they mean. */
  , throws: Map[String, Block]

  /** A map of value parameters, and a description of what they are. Typically, this provides additional information on
    * the domain of the parameters, contractual pre-conditions, etc. */
  , valueParams: Map[String, Block]

  /** A map of type parameters, and a description of what they are. Typically, this provides additional information on
    * the domain of the parameters. */
  , typeParams: Map[String, Block]

  /** The version number of the entity. There is no formatting or further meaning attached to this value. */
  , version: Option[Block]

  /** A version number of a containing entity where this member-entity was introduced. */
  , since: Option[Block]

  /** An annotation as to expected changes on this entity. */
  , todo: List[Block]

  /** An additional note concerning the contract of the entity. */
  , note: List[Block]

  /** A usage example related to the entity. */
  , example: List[Block]

  /** The comment as it appears in the source text. */
  , source: Option[String]

  /** A description for the primary constructor */
  , constructor: Option[Block]
) {

  override def toString =
    body.toString + "\n" +
    (authors map ("@author " + _.toString)).mkString("\n") +
    (result map ("@return " + _.toString)).mkString("\n") +
    (version map ("@version " + _.toString)).mkString
}
