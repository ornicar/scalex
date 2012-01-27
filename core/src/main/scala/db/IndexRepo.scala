package scalex
package db

import java.io._
import sbinary._
import DefaultProtocol._
import Operations._

import scalex.index.Def

class IndexRepo(file: String) {

  def write(defs: List[Def]) {
    //val bin = toByteArray(defs)
    //printToFile(new File(file))(_ print bin)
  }

  lazy val read: List[Def] = {
    //val d = Def("ha", "he", "ho")
    //val bin = scala.io.Source.fromFile(file).mkString
    //fromByteArray[List[Def]](bin)
    List()
  }

  def printToFile(f: File)(op: java.io.PrintWriter ⇒ Unit) {
    val p = new java.io.PrintWriter(file)
    try { op(p) } finally { p.close() }
  }
}
