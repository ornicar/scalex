package scalex
package search

import index.Def
import scalaz.NonEmptyList

case class TokenSearch(tokenIndex: TokenIndex, tokens: List[Token]) {

  type Score = Int
  type DefMap = Map[Def, Score]

  def search: List[Result] = {
    val res = defScores.toList sortWith {
      case (a, b) ⇒ a._2 > b._2
    } map {
      case (d, s) ⇒ Result(s, d)
    }
    //println(res filter (x => x.definition.tokens contains "flatmap"))
    //println(res.size)
    res
  }

  def defScores: DefMap =
    tokens.foldLeft((Map.empty, tokenIndex): (DefMap, TokenIndex)) {
      case ((acc, i), t) ⇒ {
        val defMap = tokenDefMap(Atomic(t), i)
        val accMap =
          if (acc.isEmpty) defMap
          else defMap collect {
            case (d, s) if acc contains d ⇒ d -> (s + acc(d))
          }
        (accMap, reduceIndexOnlyDefs(i, defMap.keySet))
      }
    } _1

  def reduceIndexOnlyDefs(index: TokenIndex, defs: Set[Def]) = index filterValues {
    tdefs ⇒ tdefs exists defs.contains
  }

  def reduceIndexExceptDefs(index: TokenIndex, defs: Set[Def]) = index filterValues {
    tdefs ⇒ !(tdefs exists defs.contains)
  }

  def tokenDefMap(atomic: Atomic, index: TokenIndex): DefMap = {
    List(
      (atomic.exact _, 5),
      (atomic.startWith _, 3),
      (atomic.endWith _, 2),
      (atomic.contain _, 2)
    ).foldLeft((Map.empty, index): (DefMap, TokenIndex)) {
        case ((acc, i), (func, score)) ⇒ func(i) match {
          case (found, left) ⇒ found.values.toList.flatten match {
            case Nil ⇒ (acc, left)
            case defs ⇒ {
              val defMap = defs map { _ -> score } toMap
              val accMap = acc ++ defMap
              //println(atomic + " " + defMap.size.toString + " " + i.size.toString)
              //println(accMap filter (_._1.qualifiedName endsWith "List#flatMap"))
              (accMap, left)
            }
          }
        }
      } _1
  }

  case class Atomic(token: Token) {

    def exact(i: TokenIndex) = filter(i)(token==)

    def startWith(i: TokenIndex) = filter(i) { _ startsWith token }

    def endWith(i: TokenIndex) = filter(i) { _ endsWith token }

    def contain(i: TokenIndex) = filter(i) { _ contains token }

    def filter(i: TokenIndex)(f: Token ⇒ Boolean): (TokenIndex, TokenIndex) =
      i partition {
        case (key, _) ⇒ f(key)
      }
  }
}
