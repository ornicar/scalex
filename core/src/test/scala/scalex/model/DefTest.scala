package scalex.test.model

import scalex.test.Fixture
import org.scalatest._
import scalex.model._

class DefTest extends FunSuite {

  test("Get param signature without curried method") {
    val expected = "(a: A, b: B)"
    assert(Fixture.def1.paramSignature === expected)
  }

  test("Get flattened value params with curried method") {
    val expected = "(a: A, b: B)(c: C)"
    assert(Fixture.def2.paramSignature === expected)
  }
}
