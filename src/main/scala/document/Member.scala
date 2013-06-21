package org.scalex
package document

import model.{ Project, Role }

case class Member(
    project: Project,
    parent: Parent,
    entity: model.Entity,
    role: Role,
    flags: List[model.Flag],
    resultType: model.TypeEntity) {

  def orRole(r: Role) = (role == Role.Unknown) ? copy(role = r) | this
}
