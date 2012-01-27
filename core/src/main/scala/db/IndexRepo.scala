package scalex
package db

import java.io._
import sbinary._
import Operations._

import scalex.index.Def

class IndexRepo(filename: String) extends DefaultProtocol {

  def file = new File(filename)

  implicit val defFormat = asProduct3(Def)(Def.unapply(_).get)

  def write(defs: List[Def]) { toFile(defs)(file) }

  def read: List[Def] = { fromFile[List[Def]](file) }
}
