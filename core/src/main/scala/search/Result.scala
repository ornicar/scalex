package scalex
package search

import index.Def

case class Result (
    score: Float,
    definition: Def
  ) {

  override def toString = "%s: %s".format(score, definition)
}
