package ornicar.scalex
package search.document

sealed trait TemplateRole

case object Trait extends TemplateRole
case object Class extends TemplateRole
case object CaseClass extends TemplateRole
case object Object extends TemplateRole
case object Package extends TemplateRole
