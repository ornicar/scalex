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
    val settings = new Settings(
      msg ⇒ reporter.error(FakePos("scalex"), 
      msg + "\n  scalex index -help  gives more information"),
      reporter.printMessage)
    reporter = new reporters.ConsoleReporter(settings) {
      // need to do this so that the Global instance doesn't trash all the
      // symbols just because there was an error
      override def hasErrors = false
    }
    val command = new Command(args.toList, settings)
    def hasFiles = command.files.nonEmpty 

    val destination = if (settings.d.isDefault || settings.d.value == ".") {
      val default = "database.scalex"
      reporter.warning(null, "No destination set (-d), will output to " + default)
      default
    }
    else settings.d.value
    if (settings.version.value)
      reporter.echo(versionMsg)
    else if (settings.Xhelp.value)
      reporter.echo(command.xusageMsg)
    else if (settings.Yhelp.value)
      reporter.echo(command.yusageMsg)
    else if (settings.showPlugins.value)
      reporter.warning(null, "Plugins are not available when using Scalex")
    else if (settings.showPhases.value)
      reporter.warning(null, "Phases are restricted when using Scalex")
    else if (settings.help.value || !hasFiles)
      reporter.echo(command.usageMsg)
    else try {
      val factory = new doc.DocFactory(reporter, settings)
      factory makeUniverse Left(command.files) map { universe ⇒
        val database = Universer(universe)
        Storage.write(destination, database)
        reporter.echo("Scalex database saved to " + destination)
      } getOrElse {
        reporter.error(null, "No universe found")
      }
    }
    catch {
      case ex @ FatalError(msg) ⇒
        if (settings.debug.value) ex.printStackTrace()
        reporter.error(null, "fatal error: " + msg)
    }
    finally reporter.printSummary()

    // not much point in returning !reporter.hasErrors when it has
    // been overridden with constant false.
    true
  }
}
