package ornicar.scalex
package search
package document

import model.{ Entity, TypeParam }

case class Parent(
    entity: Entity,
    typeParams: List[TypeParam]) {

  def signature = "%s%s".format(
    entity.qualifiedName,
    TypeParam show typeParams)
}
