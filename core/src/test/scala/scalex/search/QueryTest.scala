package scalex
package search

import search._

class QueryTest extends ScalexTest with WithSearch {

  "Wrong query" should {
    "empty query" in {
      analyze("") must beFailure
    }
    "unscoped text and imparsable sig" in {
      analyze("token: (") must beFailure
    }
    "Unparsable" in {
      analyze("a => (") must beFailure
    }
  }
  "dotted namespace" should {
    "translate to spaced namespace" in {
      analyze("collection.immutable.List") must beSuccess.like {
        case q => q.query.toString must_== "collection and immutable and list"
      }
    }
    "with # translate to spaced namespace" in {
      analyze("immutable.List#sorted") must beSuccess.like {
        case q => q.query.toString must_== "immutable and list and sorted"
      }
    }
  }
  "parsable unscoped text query" should {
    "tokens" in {
      analyze("list map") must beSuccess.like {
        case q ⇒ q.query.toString must_== "list and map"
      }
    }
    "scope" in {
      analyze("list map") must beSuccess.like {
        case q ⇒ q.scope.toString must_== ""
      }
    }
  }
  "parsable scoped text query" should {
    val q1 = "+scala +scalaz foldl"
    q1 in {
      "text" in {
        analyze(q1) must beSuccess.like {
          case q ⇒ q.query.toString must_== "foldl"
        }
      }
      "scope" in {
        analyze(q1) must beSuccess.like {
          case q ⇒ q.scope.toString must_== "+scala +scalaz"
        }
      }
    }
    val q2 = "foldl +scala +scalaz"
    q2 in {
      "text" in {
        analyze(q2) must beSuccess.like {
          case q ⇒ q.query.toString must_== "foldl"
        }
      }
      "scope" in {
        analyze(q2) must beSuccess.like {
          case q ⇒ q.scope.toString must_== "+scala +scalaz"
        }
      }
    }
    val q3 = "foldl -scalaz"
    q3 in {
      "text" in {
        analyze(q3) must beSuccess.like {
          case q ⇒ q.query.toString must_== "foldl"
        }
      }
      "scope" in {
        analyze(q3) must beSuccess.like {
          case q ⇒ q.scope.toString must_== "-scalaz"
        }
      }
    }
    val q4 = "-scalaz foldl"
    q4 in {
      "text" in {
        analyze(q4) must beSuccess.like {
          case q ⇒ q.query.toString must_== "foldl"
        }
      }
      "scope" in {
        analyze(q4) must beSuccess.like {
          case q ⇒ q.scope.toString must_== "-scalaz"
        }
      }
    }
  }
  "parsable unscoped mixed query" should {
    "mixed" in {
      analyze("token: a => b") must beSuccess.like {
        case q ⇒ q.query.toString must_== "token : A => B"
      }
    }
    "scope" in {
      analyze("token: a => b") must beSuccess.like {
        case q ⇒ q.scope.toString must_== ""
      }
    }
  }
  "parsable scoped mixed query" should {
    val q1 = "+scala +scalaz list foldl : List[a] => (a => b)"
    q1 in {
      "mixed" in {
        analyze(q1) must beSuccess.like {
          case q ⇒ q.query.toString must_== "list and foldl : List[A] => (A => B)"
        }
      }
      "scope" in {
        analyze(q1) must beSuccess.like {
          case q ⇒ q.scope.toString must_== "+scala +scalaz"
        }
      }
    }
  }
}
