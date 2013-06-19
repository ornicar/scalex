package org.scalex
package model

case class Def(

  /** a def is a member */
  member: Member,

  /** a def is a higher kinded */
  higherKinded: HigherKinded,

  valueParams : List[List[ValueParam]])
