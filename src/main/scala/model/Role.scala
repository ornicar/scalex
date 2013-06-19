package org.scalex
package model

sealed trait Role

object Role {

  case object Trait extends Role
  case object Class extends Role
  case object CaseClass extends Role
  case object Object extends Role
  case object Package extends Role
  case object Constructor extends Role
  case object Def extends Role
  case object Val extends Role
  case object LazyVal extends Role
  case object Var extends Role
  case object AliasType extends Role
  case object AbstractType extends Role
  case object Unknown extends Role
}
