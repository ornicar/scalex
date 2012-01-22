package scalex
package dump

import java.io._

import scalex.model._

object Store {

  val file = "store.dat"

  def write(defs: List[Def]) {
    val output = new ObjectOutputStream(new FileOutputStream(file))
    output.writeObject(defs)
    output.close
    read foreach println
  }

  def read = {
    val input = new ObjectInputStream(new FileInputStream(file))
    input.readObject().asInstanceOf[List[Def]]
  }
}
