package org.scalex
package search
package query

import document.ProjectName

private[search] case class Scope(
    include: Set[ProjectName] = Set.empty,
    exclude: Set[ProjectName] = Set.empty) {

  def +(name: ProjectName) = copy(include = include + name)

  def -(name: ProjectName) = copy(exclude = exclude + name)

  def apply(name: ProjectName): Boolean = !exclude(name) && (include.isEmpty || include(name))

  def isEmpty = include.isEmpty && exclude.isEmpty

  override def toString = 
    if (isEmpty) "*"
    else (include map ("+" + _)) ++ (exclude map ("-" + _)) mkString " "
}
