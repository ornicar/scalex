package scalex
package cli

import java.io.File

private[cli] object Parser {

  def apply = new scopt.OptionParser[Config]("scalex") {
    head("scalex", "3.0")
    cmd("index") action { (_, c) ⇒
      c.copy(mode = Update.some)
    } text ("index a library") children {
      opt[File]('d', "dir") required () valueName ("<dir>") action { (x, c) ⇒
        c.copy(dir = x.some)
      } text ("dir is a required file property")
    }
  }
}
