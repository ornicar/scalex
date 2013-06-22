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
  private lazy val defaultSelection: Projects = dropLowerVersions(all)

  private def dropLowerVersions(projects: Projects): Projects = {
    projects.sortBy(_.semVersion).foldLeft(Map[ProjectId, Project]()) {
      case (selection, project) ⇒ selection + (project.name -> project)
    }
  }.values.toList

  private def select(scope: Scope): Projects = 
    if (scope.include.nonEmpty) {
      scope.include map {
        case Area(name, version) => all filter { p =>
          p.name == name && (version.fold(true)(

    }
    }
}
