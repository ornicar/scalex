package scalex
package search

import search._

import scalaz.{ Success, Failure }

class ScopeTest extends ScalexTest with WithSearch {

  "No scope" in {
    "list map" in {
      "collection list map" finds immutable + "List#map"
    }
    "foldl" in {
      "foldl" finds "scalaz.MA#foldl"
    }
  }
  "Only scala and scalaz" in {
    "list map" in {
      "collection list map +scala +scalaz" finds immutable + "List#map"
    }
    "foldl" in {
      "foldl +scala +scalaz" finds "scalaz.MA#foldl"
    }
  }
  "Only scala" in {
    "list map" in {
      "collection list map +scala" finds immutable + "List#map"
    }
    "foldl" in {
      "foldl +scala" notFinds "scalaz.MA#foldl"
    }
  }
  "Only scalaz" in {
    "list map" in {
      "collection list map +scalaz" notFinds immutable + "List#map"
    }
    "foldl" in {
      "foldl +scalaz" finds "scalaz.MA#foldl"
    }
  }
  "without scala" in {
    "list map" in {
      "collection list map -scala" notFinds immutable + "List#map"
    }
    "foldl" in {
      "foldl -scala" finds "scalaz.MA#foldl"
    }
  }
  "without scalaz" in {
    "list map" in {
      "collection list map -scalaz" finds immutable + "List#map"
    }
    "foldl" in {
      "foldl -scalaz" notFinds "scalaz.MA#foldl"
    }
  }
  "with scala without scala" in {
    "list map" in {
      search("collection list map -scala +scala") must beFailure
    }
    "foldl" in {
      search("foldl -scala +scala") must beFailure
    }
  }
  "without scala nor scalaz" in {
    "list map" in {
      search("collection list map -scala -scalaz") must beFailure
    }
    "foldl" in {
      search("foldl -scala -scalaz") must beFailure
    }
  }
}
