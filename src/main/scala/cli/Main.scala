package ornicar.scalex
package cli

import scala.tools.nsc
import scala.util.{ Try, Success, Failure }

object Main {

  def main(args: Array[String]): Unit = sys exit {
    (process(args) failureEffect {
      case e: IllegalArgumentException ⇒ println("! %s: %s".format("Illegal argument", e.getMessage))
      case e                           ⇒ println("! " + e)
    }).isSuccess.fold(0, 1)
  }

  private def process(args: Array[String]): Try[Unit] = args.toList match {
    case "index" :: name :: version :: rest ⇒ Success(index Indexer api.Index(name, version, rest))
    case _ ⇒ Parser.parse(args) asTry badArg(args mkString " ") flatMap {
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer indexConfig)
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer api.Index.test)
      case c ⇒ Failure(badArg(c.toString))
    }
  }
}
