package scalex
package search

import index.Def

case class TokenSearch(
  tokenIndex: TokenIndex,
  tokens: List[Token]) extends IndexSearch[String, Def] {

  val keyIndex = tokenIndex

  def search = tokensFragment(tokens)

  def tokensFragment(tokens: List[Token]): Map[Def, Score] = tokens match {
    case Nil          ⇒ Map.empty
    case token :: Nil ⇒ fragment(token)
    case token :: otherTokens ⇒ {
      val othersFragment = tokensFragment(otherTokens)
      fragment(token) collect {
        case (d, s) if othersFragment contains d ⇒ (d, s + othersFragment(d))
      }
    }
  }
}
