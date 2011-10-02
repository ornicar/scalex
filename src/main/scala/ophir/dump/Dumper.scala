/* scaladoc, a documentation generator for Scala
 * Copyright 2005-2011 LAMP/EPFL
 * @author  Martin Odersky
 * @author  Geoffrey Washburn
 */

package ophir.dump
import ophir.{model, db}

import scala.tools.nsc._
import java.io.File.pathSeparator
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.FakePos
import Properties.msilLibPath

/** The main class for scaladoc, a front-end for the Scala compiler
 *  that generates documentation from source files.
 */
class Dumper {
  val versionMsg = "Scaladoc %s -- %s".format(Properties.versionString, Properties.copyrightString)

  def process(files: List[String]): Unit = {
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

    val universe = new Compiler(reporter, docSettings) universe files
    println("Extracting functions from the model...")
    val models = new ModelFactory makeModel universe
    println("Saving %d functions..." format models.size)
    //models foreach { m => println(m) }
    db.DefRepo.drop
    db.DefRepo.index
    models foreach { _ match {
      case fun: model.Def => db.DefRepo.save(fun)
    }}
  }
}
