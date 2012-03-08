package scalex
package search

import search._

class QueryTest extends ScalexTest with WithSearch {

  "Wrong query" should {
    "Empty" in {
      analyze("") must beFailure
    }
    "Unparsable" in {
      analyze("token: (") must beFailure
    }
  }
}
