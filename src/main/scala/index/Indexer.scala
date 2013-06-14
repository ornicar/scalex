package ornicar.scalex
package index

import scala.reflect.internal.util.FakePos
import scala.tools.nsc._
import scala.util.Try

/**
 * based on scala/src/scaladoc/scala/tools/nsc/Scaladoc.scala
 */
object Indexer {

  val versionMsg = "Scalex %s -- %s".format(
    Properties.versionString,
    Properties.copyrightString)

  def apply(config: api.Index) {
    process(config.args)
  }

  private def process(args: List[String]): Boolean = {
    var reporter: reporters.ConsoleReporter = null
    val docSettings = new doc.Settings(msg ⇒ reporter.error(FakePos("scalex"), msg + "\n  scalex index -help  gives more information"),
      msg ⇒ reporter.printMessage(msg))
    reporter = new reporters.ConsoleReporter(docSettings) {
      // need to do this so that the Global instance doesn't trash all the
      // symbols just because there was an error
      override def hasErrors = false
    }
    val command = new ScalaDoc.Command(args.toList, docSettings)
    def hasFiles = command.files.nonEmpty || docSettings.uncompilableFiles.nonEmpty

    if (docSettings.version.value)
      reporter.echo(versionMsg)
    else if (docSettings.Xhelp.value)
      reporter.echo(command.xusageMsg)
    else if (docSettings.Yhelp.value)
      reporter.echo(command.yusageMsg)
    else if (docSettings.showPlugins.value)
      reporter.warning(null, "Plugins are not available when using Scalex")
    else if (docSettings.showPhases.value)
      reporter.warning(null, "Phases are restricted when using Scalex")
    else if (docSettings.help.value || !hasFiles)
      reporter.echo(command.usageMsg)
    else
      try {
        val factory = new doc.DocFactory(reporter, docSettings)
        factory makeUniverse Left(command.files) map { universe ⇒
          val database = Universer(universe)
          println(database.describe)
        } getOrElse {
          reporter.error(null, "No universe found")
        }
      }
      catch {
        case ex @ FatalError(msg) ⇒
          if (docSettings.debug.value) ex.printStackTrace()
          reporter.error(null, "fatal error: " + msg)
      }
      finally reporter.printSummary()

    // not much point in returning !reporter.hasErrors when it has
    // been overridden with constant false.
    true
  }
}
