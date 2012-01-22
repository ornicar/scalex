package scalex
package dump

import scalex.model._

import net.liftweb.json.DefaultFormats
import net.liftweb.json.{ JValue }
import net.liftweb.json.JsonParser
import net.liftweb.json.Serialization.{ write ⇒ writeJson }

import scala.util.control.Exception.catching
import java.io.File

object JsonStore {

  implicit val formats = DefaultFormats // Brings in default date formats etc.

  val file = "store.json"

  def write(defs: List[Def]) {
    val json = writeJson(defs)
    printToFile(new File(file)) { w => w print json }
  }

  def read = {

    val json = scala.io.Source.fromFile(file).mkString
    println(json.size)

    val parsed = JsonParser parse json

    parsed.extract[List[Def]]
  }

  def printToFile(f: File)(op: java.io.PrintWriter ⇒ Unit) {
    val p = new java.io.PrintWriter(file)
    try { op(p) } finally { p.close() }
  }
}
