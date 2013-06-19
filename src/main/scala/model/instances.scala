package org.scalex
package model

import scalaz.Show

object instances extends instances

trait instances {

  implicit val roleShow = Show.shows[Role] {
    case Role.CaseClass    ⇒ "case class"
    case Role.LazyVal      ⇒ "lazy val"
    case Role.AliasType    ⇒ "type alias"
    case Role.AbstractType ⇒ "abstract type"
    case x                 ⇒ x.toString.toLowerCase
  }

  private def showTypeParam(tp: TypeParam): String =
    tp.variance + tp.name + (tp.hi ?? ("<:" + _)) + (tp.lo ?? (">:" + _)) + tp.typeParams.shows

  implicit val typeParamShow = Show.shows[TypeParam](showTypeParam)

  implicit val typeParamsShow = Show.shows[List[TypeParam]] { tps ⇒
    if (tps.isEmpty) ""
    else tps map showTypeParam mkString ("[", ", ", "]")
  }

  implicit val valueParamShow = Show.showA[ValueParam]

  implicit val valueParamsShow = Show.shows[List[ValueParam]] { vps ⇒
    if (vps.size > 0) vps map (_.shows) mkString ("(", ", ", ")")
    else ""
  }

  implicit val curriedValueParamsShow = Show.shows[List[List[ValueParam]]] { vps ⇒
    vps map (_.shows) mkString ""
  }
}
