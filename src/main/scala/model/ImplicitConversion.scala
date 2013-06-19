package org.scalex
package model

/** signals the member results from an implicit conversion */
case class ImplicitConversion(

  /** The source of the implicit conversion*/
  source: DocTemplate,

  /** The result type after the conversion */
  targetType: TypeEntity,

  /** The components of the implicit conversion type parents */
  targetTypeComponents: List[QualifiedName],

  /** The entity for the method that performed the conversion, if it's documented (or just its name, otherwise) */
  convertorMethod: Either[Member, String],

  /** A short name of the convertion */
  conversionShortName: String,

  /** A qualified name uniquely identifying the convertion (currently: the conversion method's qualified name) */
  conversionQualifiedName: String,

  /** The entity that performed the conversion */
  convertorOwner: Template,

  /** The constraints that the transformations puts on the type parameters */
  // constraints: List[Constraint],

  /** The members inherited by this implicit conversion */
  members: List[Member],

  /** Is this a hidden implicit conversion (as specified in the settings) */
  isHiddenConversion: Boolean)
