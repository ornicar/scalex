package org.scalex
package document

import model.{ Project, Role, Comment }

case class Member(
    projectId: String,
    parent: Parent,
    comment: Option[Comment],
    entity: model.Entity,
    role: Role,
    flags: List[model.Flag],
    resultType: model.TypeEntity) {

  // def docUrl = 

  def orRole(r: Role) = (role == Role.Unknown) ? copy(role = r) | this
}
