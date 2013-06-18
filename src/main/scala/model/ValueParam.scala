package ornicar.scalex
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

object ValueParam {

  def show(vps: List[ValueParam]): String =
    if (vps.size > 0) vps map (_.toString) mkString ("(", ", ", ")")
    else ""

  def showCurried(vps: List[List[ValueParam]]): String =
    vps map show mkString ""
}
