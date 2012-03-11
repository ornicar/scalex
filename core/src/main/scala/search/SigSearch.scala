package scalex
package search

import model._
import index.Def

case class SigSearch(sig: NormalizedTypeSig) {

  type Score = Int
  type Fragment = Map[Def, Score]

  def search: List[Result] = Nil
}
