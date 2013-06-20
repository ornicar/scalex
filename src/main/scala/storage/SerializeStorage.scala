package org.scalex
package storage

import java.io.{ File ⇒ _, _ }
import java.util.zip.{ GZIPOutputStream, GZIPInputStream }
import scala.concurrent.Future

import model._

private[storage] trait SerializeStorage extends Storage[Database] with Gzip[Database] {

  def read(file: File): Future[Database] =
    inputStream(file) { gzip ⇒
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
      }
    }

  def write(file: File, db: Database) {
    outputStream(file, db) { gzip ⇒
      val writer = new ObjectOutputStream(gzip)
      try {
        writer writeObject db
      }
      finally {
        writer.close()
      }
    }
  }
}
