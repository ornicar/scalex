package scalex
package search

import index.Def
import index.Result

case class TokenIndex(hash: Map[String, List[Def]]) {

  def matches(tokens: List[String]): List[Result] =
    ((tokens map tokenExactMatchDefs match {
      case Nil          ⇒ Nil
      case head :: tail ⇒ tail.foldLeft(head)(_ intersect _)
    }).map { Result(2, _) }) :::
    ((tokens map tokenSubstringMatchDefs match {
      case Nil          ⇒ Nil
      case head :: tail ⇒ tail.foldLeft(head)(_ intersect _)
    }).map { Result(1, _) })

  private def tokenExactMatchDefs(token: String): List[Def] = hash get token getOrElse Nil

  private def tokenSubstringMatchDefs(token: String): List[Def] =
    (hash filterKeys { k => k startsWith token }).values.flatten.toList
}

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
