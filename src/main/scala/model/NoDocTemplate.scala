package ornicar.scalex
package model

/**
 * A template (class, trait, object or package) which is referenced in the universe, but for which no further
 * documentation is available. Only templates for which a source file is given are documented by Scaladoc.
 */
case class NoDocTemplate(

    /** NoDocTemplate is a Template */
    template: Template) {

  def kind =
    if (template.isClass) "class"
    else if (template.isTrait) "trait"
    else if (template.isObject) "object"
    else ""
}
