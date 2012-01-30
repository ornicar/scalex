package scalex
package index

case class Result (
    score: Float,
    definition: Def
  ) {

  override def toString = "%s: %s".format(score, definition)
}
