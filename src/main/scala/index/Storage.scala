package ornicar.scalex
package index

import java.io._
import java.io.{ FileOutputStream, ObjectOutputStream }

import model._

object Storage {

  def read(fileName: String): Database = {
    val fileIn = new FileInputStream(fileName)
    val in = new ObjectInputStream(fileIn) {
      override def resolveClass(desc: java.io.ObjectStreamClass): Class[_] = {
        try { Class.forName(desc.getName, false, getClass.getClassLoader) }
        catch { case ex: ClassNotFoundException ⇒ super.resolveClass(desc) }
      }
    }
    try {
      in.readObject().asInstanceOf[Database]
    }
    finally {
      in.close()
      fileIn.close()
    }
  }

  def write(fileName: String, db: Database) {
    val fileOut = new FileOutputStream(fileName)
    val out = new ObjectOutputStream(fileOut)
    try {
      out.writeObject(db)
    }
    finally {
      out.close()
      fileOut.close()
    }
  }
}
