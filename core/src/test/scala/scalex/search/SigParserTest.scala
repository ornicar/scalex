package scalex.test
package search

import org.specs2.specification.Example
import scalex.search._
import scalex.model.TypeParam

class SigParserTest extends ScalexSpec {

  "Parse identity" in {
    =/=("a => a")
    =/=("A => a")
    =/=("a => A")
  }

  "Parse simple signature" in {
    =/=("a => b")
    =/=("A => b")
    =/=("a => B")
  }

  "Parse list map" in {
    =/=("list[a] => (a => b) => list[b]")
    =/=("List[e] => (e => z) => List[z]")
  }

  "Parse string toList" in {
    =/=("string => list[char]")
    =/=("String => List[Char]")
  }

  "Parse Either[A, B] => Boolean" in {
    =/=("Either[A, B] => Boolean")
  }

  "Parse multi-argument type constructors" in {
    =/=("a => (a, b)")
  }

  "Parse list[either[a, b]] => (list[a], list[b])" in {
    =/=("list[either[a, b]] => (list[a], list[b])")
  }

  private def =/=(sig: String): Example = =/=(sig, sig)

  private def =/=(sig: String, expected: String): Example =
    sig in { parse(sig) mustEqual expected }

  private def parse(str: String) = SigParser(str).fold(identity, _.toString)
}
