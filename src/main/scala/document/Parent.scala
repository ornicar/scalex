package org.scalex
package document

import model.{ Entity, TypeParam, Role, typeParamsShow }
import model.instances._

case class Parent(
    entity: Entity,
    role: Role,
    typeParams: List[TypeParam]) {

  def signature = "%s%s".format(
    entity.shortQualifiedName,
    typeParams.shows)

  def qualifiedSignature = "%s%s".format(
    entity.qualifiedName,
    typeParams.shows)
}
