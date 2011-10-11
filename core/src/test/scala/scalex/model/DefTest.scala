package scalex.test.model

import scalex.test.Fixture
import org.scalatest._
import scalex.model._

class DefTest extends FunSuite {

  test("Get flattened value params without curried method") {
    val expected = List(
      ValueParam("a", TypeEntity("A"), None, false),
      ValueParam("b", TypeEntity("B"), None, false)
    )
    assert(Fixture.def1.flattenedValueParams === expected)
  }

  test("Get flattened value params with curried method") {
    val expected = List(
      ValueParam("a", TypeEntity("A"), None, false),
      ValueParam("b", TypeEntity("B"), None, false),
      ValueParam("c", TypeEntity("C"), None, false)
    )
    assert(Fixture.def2.flattenedValueParams === expected)
  }
}
