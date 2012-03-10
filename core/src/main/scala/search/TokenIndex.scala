package scalex
package search

import index.Def

object TokenIndex {

  def apply(defs: List[Def]): TokenIndex = {

    import collection.mutable

    val hash = mutable.Map[String, mutable.ListBuffer[Def]]()

    for {
      d ← defs
      token ← d.tokens
    } hash.getOrElseUpdate(token, mutable.ListBuffer()) += d

    hash mapValues (_.toList) toMap
  }
}
