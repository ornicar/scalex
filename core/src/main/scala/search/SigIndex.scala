package scalex
package search

import index.Def

object SigIndex {

  def apply(defs: List[Def]): SigIndex = {

    import collection.mutable

    val hash = mutable.Map[String, mutable.ListBuffer[Def]]()

    for {
      d ← defs
      sig = d.signature
    } hash.getOrElseUpdate(sig, mutable.ListBuffer()) += d

    hash mapValues (_.toList) toMap
  }
}
