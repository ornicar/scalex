package org.scalex
package index

import java.io._
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }
import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import model._

private[scalex] object Storage {

  def read(file: File): Future[Database] = Future {
    val fileIn = new FileInputStream(file)
    val gzip = new GZIPInputStream(fileIn)
    val in = new ObjectInputStream(gzip) {
      override def resolveClass(desc: java.io.ObjectStreamClass): Class[_] = {
        try { Class.forName(desc.getName, false, getClass.getClassLoader) }
        catch { case ex: ClassNotFoundException ⇒ super.resolveClass(desc) }
      }
    }
    try {
      in.readObject().asInstanceOf[Database]
    }
    catch {
      case e: java.io.InvalidClassException ⇒
        throw new OutdatedDatabaseException("The database %s is too old and must be rebuilded" format file.getName)
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
