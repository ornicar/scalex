package scalex.test
package search

import scalex.search._

import org.specs2.matcher.MatchResult
import org.specs2.matcher.Matcher
import scalaz.{ Success, Failure }

class NameTest extends ScalexSpec with WithSearch {

  implicit def toMatchableSearch(search: String) = new {
    def finds(name: String): MatchResult[ValidSeq[String]] =
      searchNames(search) must findName(name)
    def findsNothing: MatchResult[ValidSeq[String]] =
      searchNames(search) must beSuccess.like {
        case names => names must beEmpty
      }
  }

  "Find by qualified name" in {
    "Natural order" in {
      "collection list map" finds "scala.collection.immutable.List#map"
    }
    "Any order" in {
      "map collection list" finds "scala.collection.immutable.List#map"
    }
    "Only 2 words" in {
      "list map" finds "scala.collection.immutable.List#map"
    }
    "Only 1 word" in {
      "mapConserve" finds "scala.collection.immutable.List#mapConserve"
    }
  }
  "Find nothing" in {
    "One known word and one unknown word" in {
      "list rantanplan".findsNothing
    }
    "Two known words and one unknown word" in {
      "collection list rantanplan".findsNothing
    }
    "Only one unknown word" in {
      "rantanplan".findsNothing
    }
  }

  private def findName(name: String): Matcher[ValidSeq[String]] = beSuccess.like {
    case names => names must contain(name)
  }
}
