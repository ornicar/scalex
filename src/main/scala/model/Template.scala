package ornicar.scalex
package model

/**
 * A template, which is either a class, trait, object or package. Depending on whether documentation is available
 * or not, the template will be modeled as a [scala.tools.nsc.doc.model.NoDocTemplate] or a
 * [scala.tools.nsc.doc.model.DocTemplateEntity].
 */
case class Template(

  /** a TemplateEntity is an Entity */
  entity: Entity,

  /** Whether this template is a package (including the root package). */
  isPackage: Boolean,

  /** Whether this template is the root package. */
  isRootPackage: Boolean,

  /** Whether this template is a trait. */
  isTrait: Boolean,

  /** Whether this template is a class. */
  isClass: Boolean,

  /** Whether this template is an object. */
  isObject: Boolean,

  /** Whether documentation is available for this template. */
  isDocTemplate: Boolean,

  /** Whether this template is a case class. */
  isCaseClass: Boolean,

  /** The self-type of this template, if it differs from the template type. */
  selfType: Option[TypeEntity])
