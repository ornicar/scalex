package scalex

import index.Def

package object search {

  type Token = String

  type TokenIndex = Map[String, List[Def]]
}
