package ornicar.scalex
package model

case class Constructor(

  /** a def is a member */
  member: Member,

  valueParams : List[List[ValueParam]])
