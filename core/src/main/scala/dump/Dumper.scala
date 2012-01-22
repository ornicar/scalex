package scalex
package dump

import scala.tools.nsc._
import java.io.File.pathSeparator
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.FakePos
import Properties.msilLibPath

import es.{ Type, Mapper }

class Dumper {

  val config = Dumper.Config(Map(
    "StringOps" -> "String"
  ))

  def process(pack: String, files: List[String], esType: Type): Unit = {
    var reporter: ConsoleReporter = null
    val docSettings = new doc.Settings(msg => reporter.error(FakePos("scaladoc"), msg + "\n  scaladoc -help  gives more information"))
    docSettings.debug.value = false
    docSettings.bootclasspath.value = (docSettings.bootclasspath.value :: jarPaths).mkString(":")
    reporter = new ConsoleReporter(docSettings) { override def hasErrors = false }

    log("Creating universe...")
    val universe = new Compiler(reporter, docSettings) universe files

    log("Extracting functions from the model...")
    val extractor = new Extractor(pack, config)
    val defs = extractor explore universe
    esType.populate(defs, Mapper.defToJson, Mapper.defToId)

    log("Indexed %d functions!" format defs.size)
  }

  private[this] def log(message: String) {
    println("* " + message)
  }

  private[this] def jarPaths: List[String] = {

    def jarPathOfClass(className: String) =
      Class.forName(className).getProtectionDomain.getCodeSource.getLocation.getFile

    List(
      jarPathOfClass("scala.tools.nsc.Interpreter"),
      jarPathOfClass("scala.ScalaObject"))
  }
}

object Dumper {

  case class Config(aliases: Map[String, String])
}
