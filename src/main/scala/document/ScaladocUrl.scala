package org.scalex
package document

import scala._

object ScaladocUrl {

  def apply(doc: Doc): Option[String] = doc match {
    case x: Template ⇒ apply(x.member, x.typeParams)
    case x: Def      ⇒ apply(x.member, x.typeParams)
    case x: Val      ⇒ apply(x.member, Nil)
  }

  // http://www.scala-lang.org/api/2.10.3/index.html#
  // scala.collection.immutable.List@
  // orElse[A1<:Int,B1>:A](PartialFunction[A1,B1]):PartialFunction[A1,B1]
  def apply(
    member: Member,
    typeParams: List[model.TypeParam]): Option[String] =
    member.project.scaladocUrl map { base ⇒
      val parent = member.parent.entity.qualifiedName
      val name = member.entity.name
      val sig = typeParams.shows
      s"$base/index.html#$parent@$name$sig"
    }
}
