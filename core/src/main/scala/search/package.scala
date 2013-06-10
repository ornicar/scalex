package scalex

import index.Def

package object search {

  type Token = String

  type Scope = Set[Token]

  type TokenIndex = Map[String, List[Def]]

  type Score = Int
}
