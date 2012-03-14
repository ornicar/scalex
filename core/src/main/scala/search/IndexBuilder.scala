package scalex
package search

import index.Def

case class ScopeIndex(nameIndex: TokenIndex, sigIndex: TokenIndex)

object IndexBuilder {

  def scopeIndexes(defs: List[Def]): Map[Scope, ScopeIndex] = {

    val packs = (defs map (_.pack)).distinct
    val scopes = permutationsInclusive(packs)
    val nameIndex = keyIndex(defs, _.tokens)
    val sigIndex = keyIndex(defs, d ⇒ List(d.signature))

    scopes map { scope ⇒
      scope -> ScopeIndex(
        filterIndex(nameIndex, scope),
        filterIndex(sigIndex, scope)
      )
    } toMap
  }

  def filterIndex(index: TokenIndex, scope: Scope): TokenIndex =
    index mapValues { defs ⇒
      defs filter { d ⇒
        scope contains d.pack
      }
    } filterValues { defs ⇒
      defs.nonEmpty
    }

  def keyIndex(defs: List[Def], defKeys: Def ⇒ List[String]): TokenIndex = {

    import scala.collection.mutable

    val hash = mutable.Map[String, mutable.ListBuffer[Def]]()

    for {
      d ← defs
      key ← defKeys(d)
    } hash.getOrElseUpdate(key, mutable.ListBuffer()) += d

    hash mapValues (_.toList) toMap
  }

  def permutationsInclusive[A](elems: Seq[A]): Set[Set[A]] = {
    val permutations = elems.permutations.toList map (_.toSet)
    val sets = for {
      s ← (1 to elems.size)
      p ← permutations
    } yield p.take(s).toSet
    sets.toSet
  }
}
