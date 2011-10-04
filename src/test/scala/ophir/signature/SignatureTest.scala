package ophir.test.signature

import org.scalatest._
import ophir.test.Fixture
import ophir.signature._
import ophir.model.TypeParam

class SignatureTest extends FunSuite {

  test("Build a simple signature") {
    val s = Signature(List(TypeParam("A"), TypeParam("B")))
    assert(s.toString === "A => B")
  }

  test("Build a signature with a type constructor") {
    val ctype = TypeParam("B", List(TypeParam("C")))
    val s = Signature(List(TypeParam("A"), ctype))
    assert(s.toString === "A => B[C]")
  }

  test("Build a signature from a Def") {
    val sig = Signature(Fixture.def1)
  }
}
