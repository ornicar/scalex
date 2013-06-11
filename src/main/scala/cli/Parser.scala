package scalex
package cli

object Parser {

  def make = new scopt.OptionParser[Config]("scalex") {
    head("scalex", "3.0")
    cmd("index") text ("Index a library") children {
      arg[File]("<dir>...") required () unbounded () action { (x, c) ⇒
        c.withIndex(_ add x)
      } text ("Directories of the code to index")
      arg[String]("<name>") required () action { (x, c) ⇒
        c.copy(index = api.Index(x).some)
      } text ("Name of the database to create")
    }
    cmd("search") action { (_, c) ⇒
      c.copy(search = api.Search().some)
    } text ("Search the index") children {
      arg[String]("<expr>...") required () unbounded () action { (x, c) ⇒
        c.withSearch(_ add x)
      } text ("Search words")
    }
  }
}
