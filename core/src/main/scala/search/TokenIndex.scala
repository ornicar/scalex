package scalex
package search

import index.Def

case class TokenIndex(hash: Map[String, List[Def]]) {

  def matches(tokens: List[String]): List[Def] = {
    tokens collect hash match {
      case Nil         ⇒ Nil
      case head :: Nil ⇒ head
      case head :: tail ⇒ {
        tail.foldLeft(head)(_ intersect _)
      }
    }
  }
}

object TokenIndex {

  def apply(defs: List[Def]): TokenIndex = {

    import collection.mutable

    val hash = mutable.Map[String, mutable.ListBuffer[Def]]()

    for {
      d ← defs
      token ← d.tokens
    } {
      if (hash contains token) hash(token) += d
      else hash.update(token, mutable.ListBuffer(d))
    }

    TokenIndex(hash mapValues (_.toList) toMap)
  }
}
