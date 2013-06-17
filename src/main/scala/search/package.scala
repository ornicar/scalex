package ornicar.scalex

package object search {

  type Token = String

  type Scope = Set[Token]

  type Score = Int

  type Results = List[Result]
}
