package org.scalex
package model

case class Comment(

    /** The main body of the comment that describes what the entity does and is.  */
    body: Block,

    /** A shorter version of the body. Usually, this is the first sentence of the body. */
    summary: Block,

    /**
     * A list of other resources to see, including links to other entities or
     * to external documentation. The empty list is used when no other resource
     * is mentionned.
     */
    see: List[Block],

    /**
     * A description of the result of the entity. Typically, this provides additional
     * information on the domain of the result, contractual post-conditions, etc.
     */
    result: Option[Block],

    /**
     * A map of exceptions that the entity can throw when accessed, and a
     * description of what they mean.
     */
    throws: Map[String, Block],

    /**
     * A map of value parameters, and a description of what they are. Typically,
     * this provides additional information on the domain of the parameters,
     * contractual pre-conditions, etc.
     */
    valueParams: Map[String, Block],

    /**
     * A map of type parameters, and a description of what they are. Typically,
     * this provides additional information on the domain of the parameters.
     */
    typeParams: Map[String, Block],

    /**
     * The version number of the entity. There is no formatting or further
     * meaning attached to this value.
     */
    version: Option[Block],

    /** A version number of a containing entity where this member-entity was introduced. */
    since: Option[Block],

    /** An annotation as to expected changes on this entity. */
    todo: List[Block],

    /**
     * Whether the entity is deprecated. Using the `@deprecated` Scala attribute
     * is prefereable to using this Scaladoc tag.
     */
    deprecated: Option[Block],

    /** An additional note concerning the contract of the entity. */
    note: List[Block],

    /** A usage example related to the entity. */
    example: List[Block],

    /** A description for the primary constructor */
    constructor: Option[Block]) {

  override def toString =
    body.toString + "\n" +
      (result map ("@return " + _.toString)).mkString("\n") +
      (version map ("@version " + _.toString)).mkString
}
