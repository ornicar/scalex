package scalex
package cli

import scala.util.{ Try, Success, Failure }

object Main {

  def main(args: Array[String]) {
    Parser.make.parse(args, Config()) asTry
      badArg(args mkString " ") flatMap {
        case Config(Some(indexConfig), _) ⇒ index Indexer indexConfig
        case c                            ⇒ Failure(badArg(c.toString))
      } recover {
        case e: IllegalArgumentException ⇒
          println("! %s: %s".format("Illegal argument", e.getMessage))
        case e ⇒ throw e
      }
  }
}
