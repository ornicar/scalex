package scalex
package search

import index.Def

case class TokenSearch(tokenIndex: TokenIndex, tokens: List[Token]) {

  type Score = Int
  type Fragment = Map[Def, Score]
  case class Filter(f: Token ⇒ Boolean)

  def search: List[Result] =
    tokensFragment(tokens).toList sortWith {
      case (a, b) ⇒ a._2 > b._2
    } map {
      case (d, s) ⇒ Result(s, d)
    }

  def tokensFragment(tokens: List[Token]): Fragment = tokens match {
    case Nil          ⇒ Map.empty
    case token :: Nil ⇒ tokenFragment(token)
    case token :: otherTokens ⇒ {
      val othersFragment = tokensFragment(otherTokens)
      tokenFragment(token) collect {
        case (d, s) if othersFragment contains d ⇒ (d, s + othersFragment(d))
      }
    }
  }

  def tokenFragment(token: Token): Fragment = {

    def scoredTokensToFragment(scoredTokens: List[(Set[Token], Score)]): Fragment = {
      for {
        tokensAndScore ← scoredTokens
        (tokens, score) = tokensAndScore
        token ← tokens.toList
        d ← tokenIndex(token)
      } yield (d -> score)
    } toMap

    val indexTokens = tokenIndex.keySet

    def tokenTokens(
      filters: List[(Token ⇒ Boolean, Score)],
      exceptTokens: Set[Token] = Set.empty): List[(Set[Token], Score)] = {

      def filterTokens(f: Token ⇒ Boolean) = (indexTokens filter f) diff exceptTokens

      filters match {
        case Nil               ⇒ Nil
        case (f, score) :: Nil ⇒ (filterTokens(f) -> score) :: Nil
        case (f, score) :: rest ⇒ {
          val foundTokens = filterTokens(f)
          val restTokens = tokenTokens(rest, exceptTokens ++ foundTokens)
          (foundTokens -> score) :: restTokens
        }
      }
    }

    scoredTokensToFragment(tokenTokens(List(
      Filter(_ == token).f -> 7,
      Filter(_ startsWith token).f -> 3,
      Filter(_ contains token).f -> 2
    )))
  }
}
