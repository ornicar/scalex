package ornicar.scalex
package model

/** A type parameter to a class, trait, or method. */
case class TypeParam(

    name: String,

    /** a TypeParam is a HigherKinded */
    higherKinded: HigherKinded,

    /** The variance of this type parameter. Valid values are "+", "-", and the empty string. */
    variance: Variance,

    /** The lower bound for this type parameter, if it has been defined. */
    lo: Option[TypeEntity],

    /** The upper bound for this type parameter, if it has been defined. */
    hi: Option[TypeEntity]) {

  override def toString =
    variance + name + (
      hi some ("<:" + _) none ""
    ) + (
        lo some (">:" + _) none ""
      ) + TypeParam.show(higherKinded.typeParams)
}

object TypeParam {

  def show(tps: List[TypeParam]) =
    if (tps.isEmpty) ""
    else tps map (_.toString) mkString ("[", ", ", "]")
}
