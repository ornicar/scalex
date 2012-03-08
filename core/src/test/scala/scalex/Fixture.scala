package scalex

import model._

trait Fixtures {

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

  val ind1 = index.Def(
    "id",
    "name",
    "qualifiedName"
  )
}
