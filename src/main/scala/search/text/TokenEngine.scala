package ornicar.scalex
package search
package text

private[search] final class TokenEngine[A](
    index: Index[A],
    heuristicBuilder: HeuristicBuilder) {

  def apply(token: Token): Fragment[A] =
    scoredTokensToFragment(scoredTokens(heuristicBuilder(token)))

  private def scoredTokensToFragment(scoredTokens: ScoredTokens): Fragment[A] = {
    for {
      keysAndScore ← scoredTokens
      (keys, score) = keysAndScore
      key ← keys.toList
      value ← index(key)
    } yield value -> score
  } toMap

  private def scoredTokens(
    heuristics: List[Heuristic],
    exclude: Set[Token] = Set.empty): List[(Tokens, Score)] = {

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

  private type ScoredTokens = List[(Tokens, Score)]
}
