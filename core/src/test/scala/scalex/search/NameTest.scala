package scalex
package search

import search._

import scalaz.{ Success, Failure }

class NameTest extends ScalexTest with WithSearch {

  implicit def toMatchableSearch(search: String) = new MatchableSearch(search)

  val immutable = new {
    def +(str: String) = "scala.collection.immutable." + str
  }

  "Find by qualified name exact matches" in {
    "Natural order" in {
      "collection list map" finds immutable+"List#map"
    }
    "Any order" in {
      "map collection list" finds immutable+"List#map"
    }
    "Only 2 words" in {
      "list map" finds immutable+"List#map"
    }
    "Only 1 word" in {
      "mapConserve" finds immutable+"List#mapConserve"
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
    "One of two" in {
      "Start" in {
        "list min" finds immutable+"List#minBy"
      }
      "Contain" in {
        "list thf".findsNothing
      }
      "End" in {
        "list index" finds immutable+"List#zipWithIndex"
      }
    }
    "Two of two" in {
      "Start" in {
        "bit hasDefinite" finds immutable+"BitSet#hasDefiniteSize"
      }
      "Contain" in {
        "bit efini".findsNothing
      }
      "End" in {
        "set size" finds immutable+"BitSet#hasDefiniteSize"
      }
    }
  }
  "Find exact match first" in {
    "Start" in {
      "list zip" finds Seq(
        immutable+"List#zip",
        immutable+"List#zipAll")
    }
    "End" in {
      "list map" finds Seq(
        immutable+"List#map",
        immutable+"List#reverseMap")
    }
  }
  "Reverse tokens" in {
    "list map" in {
      "list map" finds Seq(
        immutable+"List#map",
        immutable+"Map#toList")
    }
    "map list" in {
      "map list" finds Seq(
        immutable+"Map#toList",
        immutable+"List#map")
    }
  }
}
