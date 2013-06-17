package ornicar.scalex
package index

import java.io._
import java.io.{ FileOutputStream, ObjectOutputStream }
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }
import scala.util.{ Try, Success, Failure }

import model._

private[scalex] object Storage {

  def read(fileName: String): Try[Database] = {
    val fileIn = new FileInputStream(fileName)
    val gzip = new GZIPInputStream(fileIn)
    val in = new ObjectInputStream(fileIn) {
      override def resolveClass(desc: java.io.ObjectStreamClass): Class[_] = {
        try { Class.forName(desc.getName, false, getClass.getClassLoader) }
        catch { case ex: ClassNotFoundException ⇒ super.resolveClass(desc) }
      }
    }
    try {
      Success(in.readObject().asInstanceOf[Database])
    }
    catch {
      case e: Exception ⇒ Failure(e)
    }
    finally {
      in.close()
      gzip.close()
      fileIn.close()
    }
  }

  def write(fileName: String, db: Database) {
    val fileOut = new FileOutputStream(fileName)
    val gzip = new GZIPOutputStream(fileOut)
    val writer = new ObjectOutputStream(gzip)
    try {
      writer writeObject db
    }
    finally {
      writer.close()
      gzip.close()
      fileOut.close()
    }
  }
}
