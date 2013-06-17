package ornicar.scalex
package search
package query

private[search] case class Scope(
    include: Set[ProjectName] = Set.empty,
    exclude: Set[ProjectName] = Set.empty) {

  def +(name: ProjectName) = copy(include = include + name)

  def -(name: ProjectName) = copy(exclude = exclude + name)

  def apply(names: Seq[ProjectName]): Seq[ProjectName] = names filter apply

  def apply(name: ProjectName): Boolean = !exclude(name) && (include.isEmpty || include(name))

  override def toString =
    (include map ("+" + _)) ++ (exclude map ("-" + _)) mkString " "
}
