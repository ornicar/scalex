package scalex.test.search

import org.scalatest._
import scalex.search._
import scalex.model.TypeParam

class SigParserTest extends FunSuite {

  test("Parse identity") {
    =/=("a => a")
    =/=("A => a")
    =/=("a => A")
  }

  test("Parse simple signature") {
    =/=("a => b")
    =/=("A => b")
    =/=("a => B")
  }

  test("Parse list map") {
    =/=("list[a] => (a => b) => list[b]")
    =/=("List[e] => (e => z) => List[z]")
  }

  test("Parse string toList") {
    =/=("string => list[char]")
    =/=("String => List[Char]")
  }

  test("Parse Either[A, B] => Boolean") {
    =/=("Either[A, B] => Boolean")
  }

  test("Parse multi-argument type constructors") {
    =/=("a => (a, b)")
  }

  test("Parse list[either[a, b]] => (list[a], list[b])") {
    =/=("list[either[a, b]] => (list[a], list[b])")
  }

  private def =/=(sig: String): Unit = =/=(sig, sig)

  private def =/=(sig: String, expected: String): Unit = assert(parse(sig) === expected)

  private def parse(str: String) = SigParser(str) match {
    case Right(typeSig) => typeSig.toString
    case Left(error) => error
  }
}
