package ornicar.scalex

package object search {

  type Token = String
  type Tokens = Set[Token]

  type Score = Int

  type Fragment[A] = Map[A, Score]

  type Index[A] = Map[Token, List[A]]

  type ProjectName = String

  type Results = List[Result]

  type Heuristic = Filter Pair Score
  type HeuristicBuilder = Token ⇒ List[Heuristic]
}
