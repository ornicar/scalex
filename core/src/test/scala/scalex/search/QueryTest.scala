package scalex.test
package search

import scalex.search._

class QueryTest extends ScalexSpec with WithSearch {

  "Wrong query" should {
    "Empty" in {
      analyze("") must beAFailure
    }
    "Unparsable" in {
      analyze("token: (") must beAFailure
    }
  }
}
