package ornicar.scalex
package search
package text

private[search] final class TextEngine[A](scopedIndex: ScopingIndex[A]) {

  def search(scope: query.Scope)(tokens: Tokens) = new ScopedTextEngine(scope)

  final class ScopedTextEngine(scope: query.Scope) {

    private val tokenEngines = scopedIndex(scope) map tokenEngine

    def apply(tokens: List[Token]): Fragment[A] = tokens.map(searchToken).suml

    private def searchToken(token: Token) = tokenEngines.map(_(token)).suml

    private def tokenEngine(index: Index[A]) = new TokenEngine[A](index, token ⇒ List(
      Filter(_ == token) -> 7,
      Filter(_ startsWith token) -> 3,
      Filter(_ contains token) -> 2
    ))
  }
}
