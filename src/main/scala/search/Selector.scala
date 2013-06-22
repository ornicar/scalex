package org.scalex
package search

import semverfi.Version

import model.Project
import query.Scope

private[search] case class Selector(all: List[Project]) {

  private type Projects = List[Project]

  def apply(scope: Scope): Projects =
    if (scope.isEmpty) defaultSelection
    else select(scope)

  private lazy val defaultSelection: Projects = dropLowerVersions(all)

  // picks the greatest version of each project
  private def dropLowerVersions(projects: Projects): Projects = {
    projects.distinct.sortBy(_.semVersion).foldLeft(Map[ProjectId, Project]()) {
      case (selection, project) ⇒ selection + (project.name -> project)
    }
  }.values.toList

  private def select(scope: Scope): Projects = dropLowerVersions {
    if (scope.include.nonEmpty)
      scope.include.toList flatMap { area ⇒ all filter area.covers }
    else if (scope.exclude.nonEmpty)
      scope.exclude.toList flatMap { area ⇒ all filterNot area.covers }
    else defaultSelection
  }
}
