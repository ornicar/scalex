package org.scalex
package search
package text

import util.Timer._

private[search] final class Engine[A](scopedIndex: ScopedIndex[A]) {

  def apply(scope: query.Scope, tokens: List[Token]) =
    wrapAndMonitor("Engine search") {
      new ScopedSelector(scope)(tokens)
    }

  final class ScopedSelector(scope: query.Scope) {

    def apply(tokens: List[Token]): Fragment[A] =
      wrapAndMonitor("ScopedSelector tokens %s".format(tokens mkString ", ")) {
        tokens match {
          case Nil          ⇒ Map.empty
          case token :: Nil ⇒ searchToken(token)
          case token :: otherTokens ⇒ {
            val fragment = searchToken(token)
            val othersFragment = apply(otherTokens)
            wrapAndMonitor("ScopedSelector Merge fragment " + token) {
              fragment collect {
                case (d, s) if othersFragment contains d ⇒ (d, s + othersFragment(d))
              }
            }
          }
        }
      }

    private def searchToken(token: Token) =
      wrapAndMonitor("ScopedSelector token %s".format(token)) {
        val indices = wrapAndMonitor("ScopedSelector indices for " + scope) {
          scopedIndex filter scope
        }
        val fragments = (indices map {
          case (project, index) ⇒
            // wrapAndMonitor("TokenEngine(%s) search %s".format(project, token)) {
              tokenEngine(index)(token)
            // }
        })
        wrapAndMonitor("ScopedSelector merge fragments for token " + token) {
          fragments.suml
        }
      }

    private def tokenEngine(index: Index[A]) = new TokenEngine[A](index, token ⇒ List(
      Filter(_ == token) -> 7,
      Filter(_ startsWith token) -> 3,
      Filter(_ contains token) -> 2
    ))
  }
}
