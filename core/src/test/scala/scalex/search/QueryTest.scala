package scalex
package search

import search._

class QueryTest extends ScalexTest with WithSearch {

  "Wrong query" should {
    "empty" in {
      analyze("") must beFailure
    }
    "parsable" in {
      analyze("list map") must beSuccess.like {
        case q ⇒ q.toString must_== "list + map"
      }

    }
    "name and imparsable sig" in {
      analyze("token: (") must beFailure
    }
    "name and parsable sig" in {
      analyze("token: a => b") must beSuccess.like {
        case q ⇒ q.toString must_== "token : A => B"
      }
    }
    "Unparsable" in {
      analyze("a => (") must beFailure
    }
  }
}
