package scalex.test.model

import scalex.test.Fixture
import org.specs2.mutable._
import scalex.model._

class DefTest extends Specification {

  "Get param signature" should {
    "Without curried method" in {
      Fixture.def1.paramSignature mustEqual "(a: A, b: B)"
    }
    "With curried method" in {
      Fixture.def2.paramSignature mustEqual "(a: A, b: B)(c: C)"
    }
  }
}
