package scalex
package search

import model._
import index.Def

case class SigSearch(
  sigIndex: SigIndex,
  sig: NormalizedTypeSig) extends IndexSearch[String, Def] {

  val keyIndex = sigIndex

  def search: Fragment = fragment(sig.lowerCase)
}
