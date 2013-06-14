package ornicar.scalex
package model

/** A type parameter to a class, trait, or method. */
case class TypeParam(

  name: String,

  /** a TypeParam is a HigherKinded */
  higherKinded: HigherKinded,

  /** The variance of this type parameter. Valid values are "+", "-", and the empty string. */
  variance: String,

  /** The lower bound for this type parameter, if it has been defined. */
  lo: Option[TypeEntity],

  /** The upper bound for this type parameter, if it has been defined. */
  hi: Option[TypeEntity])
