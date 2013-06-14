package ornicar.scalex
package model

import scala.tools.nsc.doc.base.comment.Body

/**
 * An entity that is a member of a template. All entities, including templates, are member of another entity
 * except for parameters and annotations. Note that all members of a template are modelled, including those that are
 * inherited and not declared locally.
 */
case class Member(

  /** a member is an entity */
  entity: Entity,

  /** The comment attached to this member, if any. */
  comment: Option[Comment],

  /** The group this member is from */
  // def group: String

  /**
   * The flags that have been set for this entity. The following flags are supported: `implicit`, `sealed`, `abstract`,
   * and `final`.
   */
  flags: List[String],

  /** Some deprecation message if this member is deprecated, or none otherwise. */
  deprecation: Option[Body],

  /** Some migration warning if this member has a migration annotation, or none otherwise. */
  migration: Option[Body],

  /**
   * For members representing values: the type of the value returned by this member; for members
   * representing types: the type itself.
   */
  resultType: TypeEntity,

  /** Whether this member is a method. */
  isDef: Boolean,

  /** Whether this member is a value (this excludes lazy values). */
  isVal: Boolean,

  /** Whether this member is a lazy value. */
  isLazyVal: Boolean,

  /** Whether this member is a variable. */
  isVar: Boolean,

  /** Whether this member is a constructor. */
  isConstructor: Boolean,

  /** Whether this member is an alias type. */
  isAliasType: Boolean,

  /** Whether this member is an abstract type. */
  isAbstractType: Boolean,

  /** Whether this member is abstract. */
  isAbstract: Boolean,

  /**
   * If this symbol is a use case, the useCaseOf will contain the member it was derived from, containing the full
   * signature and the complete parameter descriptions.
   */
  useCaseOf: Option[Member],

  /** If this member originates from an implicit conversion, we set the implicit information to the correct origin */
  byConversion: Option[ImplicitConversion],

  /** The identity of this member, used for linking */
  signature: String,

  /** Compatibility signature, will be removed from future versions */
  signatureCompat: String,

  /** Indicates whether the member is inherited by implicit conversion */
  isImplicitlyInherited: Boolean,

  /** Indicates whether there is another member with the same name in the template that will take precendence */
  isShadowedImplicit: Boolean,

  /**
   * Indicates whether there are other implicitly inherited members that have similar signatures (and thus they all
   *  become ambiguous)
   */
  isAmbiguousImplicit: Boolean,

  /** Indicates whether the implicitly inherited member is shadowed or ambiguous in its template */
  isShadowedOrAmbiguousImplicit: Boolean)
