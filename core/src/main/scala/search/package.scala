package scalex

import index.Def

package object search {

  type Token = String

  type TokenIndex = Map[String, List[Def]]

  type SigIndex = Map[String, List[Def]]

  type Score = Int
  type Fragment = Map[Def, Score]
}
