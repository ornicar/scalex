/* scaladoc, a documentation generator for Scala
 * Copyright 2005-2011 LAMP/EPFL
 * @author  Martin Odersky
 * @author  Geoffrey Washburn
 */

package ophir.dump

import scala.tools.nsc._
//import ophir.dump.OphirDocFactory
import java.io.File.pathSeparator
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.FakePos
import Properties.msilLibPath

/** The main class for scaladoc, a front-end for the Scala compiler
 *  that generates documentation from source files.
 */
class ScalaDoc {
  val versionMsg = "Scaladoc %s -- %s".format(Properties.versionString, Properties.copyrightString)

  def process(args: Array[String]): Unit = {
    var reporter: ConsoleReporter = null
    val docSettings = new doc.Settings(msg => reporter.error(FakePos("scaladoc"), msg + "\n  scaladoc -help  gives more information"))
    docSettings.debug.value = true

    def jarPathOfClass(className: String) =
      Class.forName(className).getProtectionDomain.getCodeSource.getLocation.getFile

    val paths = List(
      jarPathOfClass("scala.tools.nsc.Interpreter"),
      jarPathOfClass("scala.ScalaObject"))
    docSettings.bootclasspath.value = (docSettings.bootclasspath.value :: paths).mkString(":")

    reporter = new ConsoleReporter(docSettings) {
      // need to do this so that the Global instance doesn't trash all the
      // symbols just because there was an error
      override def hasErrors = false
    }
    val command = new ScalaDoc.Command(args.toList, docSettings)

    val universe = new OphirDocFactory(reporter, docSettings) universe command.files
    val model = new ModelFactory makeModel universe

    println("Got models: " + model.size)
    println(model map (_.describe))
  }
}

object ScalaDoc extends ScalaDoc {
  class Command(arguments: List[String], settings: doc.Settings) extends CompilerCommand(arguments, settings) {
    override def cmdName = "scaladoc"
    override def usageMsg = (
      createUsageMsg("where possible scaladoc", false, x => x.isStandard && settings.isScaladocSpecific(x.name)) +
      "\n\nStandard scalac options also available:" +
      createUsageMsg(x => x.isStandard && !settings.isScaladocSpecific(x.name))
    )
  }

  def main(args: Array[String]): Unit = sys exit {
    process(args)
    0
  }
}
