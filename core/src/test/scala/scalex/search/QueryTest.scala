package scalex.test
package search

import scalex.search._

class QueryTest extends ScalexSpec with WithSearch {

  "Wrong query" should {
    "Empty" in {
      analyze("") must failWith(like("Empty query"))
    }
    "Unparsable" in {
      analyze("a: (") must failWith(like("failure: string matching regex"))
    }
  }
}
