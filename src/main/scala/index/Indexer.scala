package org.scalex
package index

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.reflect.internal.util.FakePos
import scala.tools.nsc
import scala.util.Try

/**
 * based on scala/src/scaladoc/scala/tools/nsc/Scaladoc.scala
 */
private[scalex] object Indexer {

  def apply(config: api.Index) {
    process(config.name, config.version, config.args)
  }

  private def process(name: String, version: String, args: List[String]): Boolean = {
    var reporter: nsc.reporters.ConsoleReporter = null
    val settings = new Settings(
      msg ⇒ reporter.error(FakePos("scalex"), msg + "\n  scalex index -help  gives more information"),
      reporter.printMessage)
    reporter = new nsc.reporters.ConsoleReporter(settings) {
      // need to do this so that the Global instance doesn't trash all the
      // symbols just because there was an error
      override def hasErrors = false
    }
    val command = new Command(args.toList, settings)
    def hasFiles = command.files.nonEmpty

    val outputFile = new File(
      if (settings.outputFile.isDefault || settings.outputFile.value == ".")
        name + "_" + version + ".scalex"
      else settings.outputFile.value
    )
    if (settings.version.value)
      reporter.echo("scalex 3")
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
      val project = model.Project(name, version) getOrElse {
        throw new nsc.FatalError("Invalid format, should be name_version")
      }
      val factory = new nsc.doc.DocFactory(reporter, settings)
      println("- Index %d scala files" format command.files.length)
      factory makeUniverse Left(command.files) map { universe ⇒
        println("- Build scalex database")
        val root = new Mapper() docTemplate universe.rootPackage
        val seed = model.Seed(project, root)
        val database = new model.Database(List(seed))
        println("- Compress database")
        storage.Storage.write(outputFile, database)
        println("- Sanity check")
        val check = Await.result(
          storage.Storage.read(outputFile) map (_ ⇒ true) recover { case _ ⇒ false },
          1 minute)
        if (!check) {
          throw new nsc.FatalError("Database looks corrupted")
        }
        println("- Success!")
        println("- Database saved to " + outputFile.getAbsolutePath)
      } getOrElse {
        reporter.error(null, "No universe found")
      }
    }
    catch {
      case ex @ nsc.FatalError(msg) ⇒
        if (settings.debug.value) ex.printStackTrace()
        reporter.error(null, "fatal error: " + msg)
    }
    finally reporter.printSummary()

    // not much point in returning !reporter.hasErrors when it has
    // been overridden with constant false.
    true
  }
}
