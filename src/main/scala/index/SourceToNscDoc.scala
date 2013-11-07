package org.scalex
package index

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.reflect.internal.util.FakePos
import scala.tools.nsc, nsc.doc.model.DocTemplateEntity
import scala.util.{ Try, Success, Failure }

private[scalex] object SourceToNscDoc {

  def apply(
    name: String,
    version: String,
    settings: Settings,
    args: List[String]): Try[DocTemplateEntity] = Try {
    var reporter: nsc.reporters.ConsoleReporter = null
    reporter = new nsc.reporters.ConsoleReporter(settings) {
      // need to do this so that the Global instance doesn't trash all the
      // symbols just because there was an error
      override def hasErrors = false
    }
    val command = new Command(args.toList, settings)
    def hasFiles = command.files.nonEmpty
    try {
      val factory = new nsc.doc.DocFactory(reporter, settings)
      factory makeUniverse Left(command.files) map (_.rootPackage) getOrElse {
        throw new Exception("No universe found")
      }
    }
    catch {
      case ex @ nsc.FatalError(msg) ⇒
        if (true || settings.debug.value) ex.printStackTrace()
        reporter.error(null, "fatal error: " + msg)
        throw ex
    }
    finally reporter.printSummary()
  }
}
