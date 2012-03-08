package scalex
package search

import index.Def

case class TokenIndex(hash: Map[String, List[Def]])

object TokenIndex {

  def apply(defs: List[Def]): TokenIndex = {

    import collection.mutable

    val hash = mutable.Map[String, mutable.ListBuffer[Def]]()

    for {
      d ← defs
      token ← d.tokens
    } hash.getOrElseUpdate(token, mutable.ListBuffer()) += d

    TokenIndex(hash mapValues (_.toList) toMap)
  }
}
