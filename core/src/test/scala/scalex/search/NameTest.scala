package scalex.test.search

import org.specs2.mutable._
import org.specs2.matcher.MatchResult
import scalex.search._

class NameTest extends Specification with WithSearch {

  "Find by name" in {
    searchNames("map") must findName("scala.collection.immutable.List#map")
  }

  def findName(result: String) = beRight.like(findPartialFunction(result))

  def findPartialFunction(result: String): PartialFunction[Seq[String], MatchResult[Seq[String]]] = {
    case results => results must contain(result)
  }
}
