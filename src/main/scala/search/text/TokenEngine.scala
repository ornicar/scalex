package org.scalex
package search
package text

import util.Timer._

private[search] final class TokenEngine[A](
    index: Index[A],
    heuristicBuilder: HeuristicBuilder) {

  private type ScoredTokens = List[(Tokens, Score)]

  def apply(token: Token): Fragment[A] =
    buildFragment(scoreTokens(heuristicBuilder(token)))

  private def buildFragment(scoredTokens: ScoredTokens): Fragment[A] =
    // wrapAndMonitor("TokenEngine build fragment") {
    (for {
      tokensAndScore ← scoredTokens
      (tokens, score) = tokensAndScore
      token ← tokens
      value ← index(token)
    } yield value -> score).toMap
  // }

  private def scoreTokens(heuristics: List[Heuristic]) =
    // wrapAndMonitor("TokenEngine score tokens") {
    scoredTokens(heuristics, Set.empty)
  // }

  private def scoredTokens(
    heuristics: List[Heuristic],
    exclude: Set[Token]): List[(Tokens, Score)] = {

    def filterToken(f: Filter) = index.keys diff exclude filter f.apply

    heuristics match {
      case Nil               ⇒ Nil
      case (f, score) :: Nil ⇒ (filterToken(f) -> score) :: Nil
      case (f, score) :: rest ⇒ {
        val foundToken = filterToken(f)
        (foundToken -> score) :: scoredTokens(rest, exclude ++ foundToken)
      }
    }
  }
}
