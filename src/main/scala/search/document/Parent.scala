package org.scalex
package search
package document

import model.{ Entity, TypeParam, Role, typeParamsShow }

case class Parent(
    entity: Entity,
    role: Role,
    typeParams: List[TypeParam]) {

  def signature = "%s%s".format(
    entity.shortQualifiedName,
    typeParams.shows)
}
