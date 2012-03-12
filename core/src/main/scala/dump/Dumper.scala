package scalex
package dump

import db.DefRepo

import scala.tools.nsc._
import java.io.File.pathSeparator
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.FakePos
import Properties.msilLibPath

class Dumper(defRepo: DefRepo) {

  val aliases = Map(
    "StringOps" -> "String"
  )

  def process(pack: String, files: List[String], sourceBase: String): Unit = {
    var reporter: ConsoleReporter = null
    val docSettings = new doc.Settings(msg ⇒ reporter.error(FakePos("scaladoc"), msg + "\n  scaladoc -help  gives more information"))
    docSettings.debug.value = false
    docSettings.bootclasspath.value = (docSettings.bootclasspath.value :: jarPaths).mkString(":")
    reporter = new ConsoleReporter(docSettings) { override def hasErrors = false }
    val config = Dumper.Config(aliases, sourceBase)

    log("Creating universe...")
    val universe = new Compiler(reporter, docSettings) universe files

    log("Dropping previous pack...")
    defRepo.removePack(pack)

    log("Extracting functions from the model...")
    (new Extractor(pack, config)).passFunctions(universe, defRepo.batchInsert)

    log("Saved %d functions!" format defRepo.count())
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

  case class Config(aliases: Map[String, String], sourceBase: String) {

    val FileRegex = ("""^.*(%s.+)$""" format sourceBase).r.pp

    def patchSourceFile(file: String): Option[String] = file.pp match {
      case FileRegex(path) => Some(path.pp)
      case _ => None
    }
  }
}
