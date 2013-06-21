package org.scalex
package search

import semverfi.Version

import model.Project
import query.Scope

private[search] case class Selector(all: List[Project]) {

  private type Projects = List[Project]

  def apply(scope: Scope): Projects =
    if (scope.isEmpty) defaultSelection
    else defaultSelection // select(scope)

  // picks the greatest version of each project
  private lazy val defaultSelection: Projects = filterLowerVersions(all)

  private def filterLowerVersions(projects: Projects): Projects = {
    projects.sortBy(_.version).foldLeft(Map[ProjectId, Project]()) {
      case (selection, project) ⇒ selection + (project.name -> project)
    }
  }.values.toList

  // private def select(scope: Scope) = 
}
