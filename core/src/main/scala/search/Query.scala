package scalex
package search

import scalaz.NonEmptyList

import model.NormalizedTypeSig

trait Query

case class MixQuery(
    tokens: NonEmptyList[String],
    sig: NormalizedTypeSig) extends Query {
  override def toString = "%s : %s".format(tokens.list mkString " and ", sig)
}

case class NameQuery(tokens: NonEmptyList[String]) extends Query {
  override def toString = tokens.list mkString " and "
}

case class SigQuery(sig: NormalizedTypeSig) extends Query {
  override def toString = sig.toString
}

case class ScopedQuery(query: Query, scope: QueryScope)

case class QueryScope(
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
