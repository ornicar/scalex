package scalex.test
package search

import scalex.search._

import scalaz.{ Success, Failure }

class NameTest extends ScalexSpec with WithSearch {

  implicit def toMatchableSearch(search: String) = new MatchableSearch(search)

  "Find by qualified name exact matches" in {
    "Natural order" in {
      "collection list map" finds "collection.immutable.List#map"
    }
    "Any order" in {
      "map collection list" finds "collection.immutable.List#map"
    }
    "Only 2 words" in {
      "list map" finds "collection.immutable.List#map"
    }
    "Only 1 word" in {
      "mapConserve" finds "collection.immutable.List#mapConserve"
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
  "Find by partial match" in {
    "Start" in {
      "list min" finds "collection.immutable.List#minBy"
    }
    "Contain" in {
      "list thf".findsNothing
    }
    "End" in {
      "list index" finds "collection.immutable.List#zipWithIndex"
    }
  }
  "Find exact match first" in {
    "Start" in {
      "list zip" finds Seq(
        "collection.immutable.List#zip",
        "collection.immutable.List#zipAll")
    }
    "End" in {
      "list map" finds Seq(
        "collection.immutable.List#map",
        "collection.immutable.List#reverseMap")
    }
  }
  "Reverse tokens" in {
    "list map" in {
      "list map" finds Seq(
        "collection.immutable.List#map",
        "collection.immutable.Map#toList")
    }
    "map list" in {
      "map list" finds Seq(
        "collection.immutable.Map#toList",
        "collection.immutable.List#map")
    }
  }
}
