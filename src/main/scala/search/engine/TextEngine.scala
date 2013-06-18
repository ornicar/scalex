package ornicar.scalex
package search
package engine

private[search] final class TextEngine[A](index: Index[A]) {

  private val tokenEngine = new TokenEngine[A](index, token ⇒ List(
    ((_ == token): Filter) -> 7,
    ((_ startsWith token): Filter) -> 3,
    ((_ contains token): Filter) -> 2
  )) 

  def search(tokens: List[Token]): Fragment[A] = tokens match {
    case Nil          ⇒ Map.empty
    case token :: Nil ⇒ tokenEngine(token)
    case token :: otherTokens ⇒ {
      val othersFragment = search(otherTokens)
      tokenEngine(token) collect {
        case (d, s) if othersFragment contains d ⇒ (d, s + othersFragment(d))
      }
    }
  }
}
