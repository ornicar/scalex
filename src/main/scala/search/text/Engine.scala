package org.scalex
package search
package text

private[search] final class Engine[A](scopedIndex: ScopedIndex[A]) {

  def apply(scope: query.Scope) = new ScopedSelector(scope)

  final class ScopedSelector(scope: query.Scope) {

    private val tokenEngines = scopedIndex(scope) map tokenEngine

    def apply(tokens: List[Token]): Fragment[A] = tokens match {
      case Nil ⇒ Map.empty
      case token :: Nil ⇒ searchToken(token)
      case token :: otherTokens ⇒ {
        val othersFragment = apply(otherTokens)
        searchToken(token) collect {
          case (d, s) if othersFragment contains d ⇒ (d, s + othersFragment(d))
        }
      }
    }

    private def searchToken(token: Token) = tokenEngines.map(_(token)).suml

    private def tokenEngine(index: Index[A]) = new TokenEngine[A](index, token ⇒ List(
      Filter(_ == token) -> 7,
      Filter(_ startsWith token) -> 3,
      Filter(_ contains token) -> 2
    ))
  }
}
