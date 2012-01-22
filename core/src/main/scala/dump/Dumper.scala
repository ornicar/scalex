package scalex.dump

import scalex.db.DefRepo

import scala.tools.nsc._
import java.io.File.pathSeparator
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.FakePos
import Properties.msilLibPath

class Dumper {

  val config = Dumper.Config(Map(
    "StringOps" -> "String"
  ))

  def process(pack: String, files: List[String]): Unit = {
    var reporter: ConsoleReporter = null
    val docSettings = new doc.Settings(msg ⇒ reporter.error(FakePos("scaladoc"), msg + "\n  scaladoc -help  gives more information"))
    docSettings.debug.value = false
    docSettings.bootclasspath.value = (docSettings.bootclasspath.value :: jarPaths).mkString(":")
    reporter = new ConsoleReporter(docSettings) { override def hasErrors = false }

    log("Creating universe...")
    val universe = new Compiler(reporter, docSettings) universe files

    log("Dropping previous pack...")
    DefRepo.removePack(pack)

    log("Extracting functions from the model...")
    (new Extractor(println, pack, config)).passFunctions(universe, DefRepo.batchInsert)

    log("Saved %d functions!" format DefRepo.count())
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
