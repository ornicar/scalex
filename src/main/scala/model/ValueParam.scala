package ornicar.scalex
package model

/** A value parameter to a constructor or method. */
case class ValueParam(

  name: String,

  /** The type of this value parameter. */
  resultType: TypeEntity,

  /** The devault value of this value parameter, if it has been defined. */
  defaultValue: Option[String],

  /** Whether this value parameter is implicit. */
  isImplicit: Boolean)
