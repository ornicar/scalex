package ornicar.scalex
package cli

import scala.tools.nsc
import scala.util.{ Try, Success, Failure }

object Main {

  final class Command(
      args: List[String],
      settings: nsc.doc.Settings) extends nsc.CompilerCommand(args, settings) {
    override def cmdName = "scalex"
    override def usageMsg = (
      createUsageMsg("where possible scalex", shouldExplain = false, x ⇒ x.isStandard && settings.isScaladocSpecific(x.name)) +
      "\n\nStandard scalac options also available:" +
      createUsageMsg(x ⇒ x.isStandard && !settings.isScaladocSpecific(x.name))
    )
  }

  def main(args: Array[String]): Unit = sys exit {
    println(args.toList)
    (process(args) failureEffect {
      case e: IllegalArgumentException ⇒ println("! %s: %s".format("Illegal argument", e.getMessage))
      case e                           ⇒ println("! " + e)
    }).isSuccess.fold(0, 1)
  }

  private def process(args: Array[String]): Try[Unit] =
    Parser.parse(args) asTry badArg(args mkString " ") flatMap {
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer indexConfig)
      case Config(Some(indexConfig), _) ⇒ Success(index Indexer api.Index.test)
      case c                            ⇒ Failure(badArg(c.toString))
    }
}
