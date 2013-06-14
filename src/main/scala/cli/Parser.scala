package ornicar.scalex
package cli

object Parser {

  def parse(args: Array[String]) = (new scopt.OptionParser[Config]("scalex") {
    head("scalex", "3.0")
    cmd("index") text ("Index a library") action { (_, c) ⇒
      c.copy(index = api.Index(Nil).some)
    } children {
      arg[String]("<arg>") required () unbounded () action { (x, c) ⇒
        c.withIndex(_ add x)
      } text ("Compiler arguments")
    }
    cmd("search") action { (_, c) ⇒
      c.copy(search = api.Search().some)
    } text ("Search the index") children {
      arg[String]("<expr>...") required () unbounded () action { (x, c) ⇒
        c.withSearch(_ add x)
      } text ("Search words")
    }
  }) parse (args, Config())
}
