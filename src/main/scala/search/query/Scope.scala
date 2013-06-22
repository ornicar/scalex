package org.scalex
package search
package query

private[search] case class Scope(
    include: Set[Area] = Set.empty,
    exclude: Set[Area] = Set.empty) {

  def +(str: String): Scope = this + Area(str)
  def +(area: Area): Scope = copy(include = include + area)

  def -(str: String): Scope = this - Area(str)
  def -(area: Area): Scope = copy(exclude = exclude + area)

  def isEmpty = include.isEmpty && exclude.isEmpty

  override def toString =
    if (isEmpty) "*"
    else (include map ("+" + _)) ++ (exclude map ("-" + _)) mkString " "
}
