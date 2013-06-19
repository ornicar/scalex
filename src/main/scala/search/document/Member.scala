package ornicar.scalex
package search
package document

import model.Role

case class Member(
    project: Project,
    parent: Parent,
    entity: model.Entity,
    role: Role,
    flags: List[model.Flag],
    resultType: model.TypeEntity) {

  def orRole(r: Role) = (role == Role.Unknown) ? copy(role = r) | this
}
