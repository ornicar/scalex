package org.scalex
package model

// import scala.tools.nsc.doc.base.comment.Body

/**
 * An entity that is a member of a template. All entities, including templates, are member of another entity
 * except for parameters and annotations. Note that all members of a template are modelled, including those that are
 * inherited and not declared locally.
 */
case class Member(

  /** a member is an entity */
  entity: Entity,

  /** The comment attached to this member, if any. */
  // comment: Option[Comment],

  /** The group this member is from */
  // def group: String

  /** The qualified name of the member in its currently active declaration template. */
  // definitionName: String,

  /** The templates in which this member has been declared. The first element of the list is the template that contains
    * the currently active declaration of this member, subsequent elements are declarations that have been overriden. If
    * the first element is equal to `inTemplate`, the member is declared locally, if not, it has been inherited. All
    * elements of this list are in the linearization of `inTemplate`. */
  inDefinitionTemplates: List[QualifiedName],

  /**
   * The flags that have been set for this entity. The following flags are supported: 
   * `implicit`, `sealed`, `abstract`, `deprecated`, `migration` and `final`.
   */
  flags: List[Flag],

  /**
   * For members representing values: the type of the value returned by this member; for members
   * representing types: the type itself.
   */
  resultType: TypeEntity,

  /** def, val, lazy val, alias type, ... */
  role: Role,

  /**
   * If this symbol is a use case, the useCaseOf will contain the member it was derived from, containing the full
   * signature and the complete parameter descriptions.
   */
  // useCaseOf: Option[Member],

  /** If this member originates from an implicit conversion, we set the implicit information to the correct origin */
  byConversion: Option[ImplicitConversion],

  /** The identity of this member, used for linking */
  // signature: String,

  /** Compatibility signature, will be removed from future versions */
  // signatureCompat: String,

  /** Indicates whether the member is inherited by implicit conversion */
  isImplicitlyInherited: Boolean)

  /** Indicates whether there is another member with the same name in the template that will take precendence */
  // isShadowedImplicit: Boolean,

  /**
   * Indicates whether there are other implicitly inherited members that have similar signatures (and thus they all
   *  become ambiguous)
   */
  // isAmbiguousImplicit: Boolean,

  /** Indicates whether the implicitly inherited member is shadowed or ambiguous in its template */
  // isShadowedOrAmbiguousImplicit: Boolean
