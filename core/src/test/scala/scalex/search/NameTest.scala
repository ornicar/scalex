package scalex.test.search

import scalex.search._

import org.specs2.mutable._
import org.specs2.matcher.MatchResult
import org.specs2.matcher.Matcher
import scalaz.{ Success, Failure }

class NameTest extends Specification with WithSearch with ScalazMatchers {

  "Find by name" in {
    searchNames("list map") must findName("scala.collection.immutable.List#map")
  }

  private def findName(name: String): Matcher[ValidSeq[String]] = succeedWith(defName(name))

  private def defName(name: String): PartialFunction[Seq[String], MatchResult[Seq[String]]] = {
    case names => names must contain(name)
  }
}
