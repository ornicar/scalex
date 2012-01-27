package scalex.test
package search

import scalex.search._

import org.specs2.matcher.MatchResult
import org.specs2.matcher.Matcher
import scalaz.{ Success, Failure }

class NameTest extends ScalexSpec with WithSearch {

  "Wrong query" should {
    "Empty" in {
      searchNames("") must beAFailure
    }
    "Unparsable" in {
      searchNames("a: (") must beAFailure
    }
  }
  "Valid query" should {
    "Text query" in {
      "Find by qualified name" in {
        searchNames("collection list map") must beSuccess.like {
          case names => names must contain("scala.collection.immutable.List#map")
        }
      }
    }
  }

  private def findName(name: String): Matcher[ValidSeq[String]] = succeedWith(defName(name))

  private def defName(name: String): PartialFunction[Seq[String], MatchResult[Seq[String]]] = {
    case names => names must contain(name)
  }
}
