package org.scalex

package object storage {

  private[storage] val encoding = "UTF-8"

  private[scalex] object api {

    case object GetProjects

    case class GetSeed(project: model.Project)
  }
}
