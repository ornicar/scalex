package org.scalex

package object search {

  type Score = Int

  type Fragment[A] = Map[A, Score]

  def fragmentMonoid[A] = scalaz.Monoid[Fragment[A]]
}

package search {

  import com.sksamuel.elastic4s.source.Source

  private[search] final class JsonSource(
    root: play.api.libs.json.JsObject
  ) extends Source {
    def json = root.toString
  }
}
