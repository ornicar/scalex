package ornicar.scalex
package search
package text

private[text] final class Index[A](hash: Map[Token, List[A]]) {

  lazy val keys = hash.keySet

  def apply(token: Token): List[A] = ~(hash get token)
}
