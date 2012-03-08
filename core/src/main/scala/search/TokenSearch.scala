package scalex
package search

import index.Def
import index.Result

case class TokenSearch(index: TokenIndex, tokens: List[String]) {

  def search: List[Result] = {
    exactMatches ::: substringMatches
  }

  private def exactMatches: List[Result] =
    ((tokens map exactMatch match {
      case Nil          ⇒ Nil
      case head :: tail ⇒ tail.foldLeft(head)(_ intersect _)
    }).map { Result(2, _) })

  private def substringMatches: List[Result] =
    ((tokens map substringMatch match {
      case Nil          ⇒ Nil
      case head :: tail ⇒ tail.foldLeft(head)(_ intersect _)
    }).map { Result(1, _) })

  private def exactMatch(token: String): List[Def] =
    index.hash get token getOrElse Nil

  private def substringMatch(token: String): List[Def] =
    (index.hash filterKeys { k => k startsWith token }).values.flatten.toList
}
