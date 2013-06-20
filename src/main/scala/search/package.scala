package org.scalex

package object search {

  type Score = Int

  type Fragment[A] = Map[A, Score]

  def fragmentMonoid[A] = scalaz.Monoid[Fragment[A]]

  type Results = List[Result]

  type Heuristic = Filter Pair Score
  type HeuristicBuilder = Token ⇒ List[Heuristic]
}
