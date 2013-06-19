package org.scalex
package model

/** A value parameter to a constructor or method. */
case class ValueParam(

    name: String,

    /** The type of this value parameter. */
    resultType: TypeEntity,

    /** The devault value of this value parameter, if it has been defined. */
    defaultValue: Option[Expression],

    /** Whether this value parameter is implicit. */
    isImplicit: Boolean) {

  override def toString = (
    if (isImplicit) "implicit " else ""
  ) + name + ": " + resultType.toString + (defaultValue match {
      case Some(dv) ⇒ " = " + dv
      case None     ⇒ ""
    })
}
