package scalex
package model

import model._

class DefTest extends ScalexTest with Fixtures {

  "Get param signature" should {
    "Without curried method" in {
      def1.paramSignature mustEqual "(a: A, b: B)"
    }
    "With curried method" in {
      def2.paramSignature mustEqual "(a: A, b: B)(c: C)"
    }
  }
}
