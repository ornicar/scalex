package ornicar.scalex
package model

sealed trait TemplateRole

object TemplateRole {

  case object Trait extends TemplateRole
  case object Class extends TemplateRole
  case object CaseClass extends TemplateRole
  case object Object extends TemplateRole
  case object Package extends TemplateRole
  case object Unknown extends TemplateRole
}
