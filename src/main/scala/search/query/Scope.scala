package ornicar.scalex
package search
package query

private[search] case class QueryScope(
    only: Set[String] = Set.empty,
    without: Set[String] = Set.empty) {

  def +(pack: String) = copy(only = only + pack)

  def -(pack: String) = copy(without = without + pack)

  def matchScope(scopes: Seq[Scope]): Option[Scope] =
    scopes sortBy (-_.size) find { scope ⇒
      {
        if (only.isEmpty) true else scope forall only.contains
      } && {
        if (without.isEmpty) true else scope forall (s ⇒ !(without contains s))
      }
    }

  override def toString =
    (only map ("+" + _)) ++ (without map ("-" + _)) mkString " "
}
