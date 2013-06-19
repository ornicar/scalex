package org.scalex
package model

case class Def(

  /** a def is a member */
  member: Member,

  /** a def is a higher kinded */
  typeParams: List[TypeParam],

  valueParams : List[List[ValueParam]])
