package ornicar.scalex
package model

/** An entity that is parameterized by types */
case class HigherKinded(

  /** The type parameters of this entity. */
  typeParams: List[TypeParam])
