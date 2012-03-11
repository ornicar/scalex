package scalex
package search

import search._

import scalaz.{ Success, Failure }

class TypeTest extends ScalexTest with WithSearch {

  implicit def toMatchableSearch(search: String) = new MatchableSearch(search)

  val immutable = new {
    def +(str: String) = "scala.collection.immutable." + str
  }

  def duplicates(list: Seq[_]) = list diff (list.distinct)

  "No duplicated functions" in {
    "simple type" in {
      search("string => int", 200) must beSuccess.like {
        case defs ⇒ {
          duplicates(defs) must_== Nil
        }
      }
    }
  }
  "a => b" in {
    "string => int" in {
      "string => int" finds immutable + "StringOps#size"
    }
  }
  "a => b => c" in {
    "string => int => string" in {
      "string => int => string" finds immutable + "StringOps#drop"
    }
  }
  "m[a] => a" in {
    "list[a] => a" in {
      "list[a] => a" finds immutable + "List#head"
    }
  }
  "m[a] => n[a]" in {
    "list[a] => option[a]" in {
      "list[a] => option[a]" finds immutable + "List#headOption"
    }
  }
  "Find nothing" in {
    "One known type and one unknown type" in {
      "string => rantanplan".findsNothing
    }
    "Two known words and one unknown word" in {
      "string => rantanplan => string".findsNothing
    }
    "Only one unknown word" in {
      ": rantanplan".findsNothing
    }
  }
}
