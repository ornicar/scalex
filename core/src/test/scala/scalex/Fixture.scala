package scalex.test

import scalex.model._

object Fixture {

  val def1 = Def(
    "id",
    "test",
    "",
    "",
    "",
    Parent("ptest", "", Nil, false),
    SimpleClass("ParentClass", true),
    None,
    List(
      ValueParams(List(
        ValueParam("a", SimpleClass("A", false), None, false),
        ValueParam("b", SimpleClass("B", false), None, false)
      ))
    ),
    Nil,
    "test",
    None
  )

  val def2 = def1.copy(
    valueParams = List(
      ValueParams(List(
        ValueParam("a", SimpleClass("A", false), None, false),
        ValueParam("b", SimpleClass("B", false), None, false)
      )),
      ValueParams(List(
        ValueParam("c", SimpleClass("C", false), None, false)
      ))
    )
  )
}
