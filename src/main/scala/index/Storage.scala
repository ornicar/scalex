package ornicar.scalex
package index

import java.io._
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }
import scala.util.{ Try, Success, Failure }

import model._

private[scalex] object Storage {

  def read(file: File): Try[Database] = {
    val fileIn = new FileInputStream(file)
    val gzip = new GZIPInputStream(fileIn)
    val in = new ObjectInputStream(gzip) {
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

  def write(file: File, db: Database) {
    file.delete()
    val fileOut = new FileOutputStream(file)
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
