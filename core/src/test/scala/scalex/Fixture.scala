package scalex.test

import scalex.model._

object Fixture {

  val def1 = Def(
    "test",
    "",
    Parent("ptest", "", Nil, false),
    TypeEntity("ttest"),
    "test comment",
    List(
      ValueParams(List(
        ValueParam("a", TypeEntity("A"), None, false),
        ValueParam("b", TypeEntity("B"), None, false)
      ))
    ),
    Nil,
    Nil
  )

  val def2 = def1.copy(
    valueParams = List(
      ValueParams(List(
        ValueParam("a", TypeEntity("A"), None, false),
        ValueParam("b", TypeEntity("B"), None, false)
      )),
      ValueParams(List(
        ValueParam("c", TypeEntity("C"), None, false)
      ))
    )
  )
}
