package org.scalex

package object search {

  type Token = String
  type Tokens = Set[Token]

  type Score = Int

  type Fragment[A] = Map[A, Score]

  def fragmentMonoid[A] = scalaz.Monoid[Fragment[A]]

  type ProjectName = String
  type ProjectId = String

  type Results = List[Result]

  type Heuristic = Filter Pair Score
  type HeuristicBuilder = Token ⇒ List[Heuristic]
}
