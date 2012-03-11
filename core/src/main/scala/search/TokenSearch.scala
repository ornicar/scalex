package scalex
package search

import index.Def
import scalaz.NonEmptyList

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

    def indexToFragment(index: TokenIndex, score: Score): Fragment =
      index.values.toList.flatten map (_ -> score) toMap

    def tokenFilterFragment(
      index: TokenIndex,
      filters: List[(Token ⇒ Boolean, Score)]): Fragment =
      filters match {
        case Nil                    ⇒ Map.empty
        case (filter, score) :: Nil ⇒ indexToFragment(index filterKeys filter, score)
        case (filter, score) :: otherFilters ⇒
          val (found, left) = index partition { case (key, _) ⇒ filter(key) }
          tokenFilterFragment(left, otherFilters) ++ indexToFragment(found, score)
      }

    tokenFilterFragment(tokenIndex, List(
      Filter(_ == token).f -> 7,
      Filter(_ startsWith token).f -> 3,
      Filter(_ contains token).f -> 2
    ))
  }
}
