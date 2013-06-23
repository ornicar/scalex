package org.scalex
package model

sealed trait Role {

  def name = toString.toLowerCase
}

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

  private def all: List[Role] = List(Trait, Class, CaseClass, Object, Package, Constructor, Def, Val, LazyVal, Var, AliasType, AbstractType, Unknown)

  private[scalex] def fromName(str: String): Role = {
    val lc = str.toLowerCase
    all.find(_.name == lc) | Unknown
  }

  private[scalex] def toName(role: Role): String = role.name
}
