package org.scalex
package storage
package binary

import java.io.{ File ⇒ _, _ }
import scala.concurrent.Future

import model.{ Database, Header }

// private[storage] object BinaryFileStorage extends Storage[Header, Database] with Gzip[Header, Database] {

//   import BinaryProtocol._

//   def header(file: File): Future[Header] =
//     bufferedInputStream(file) { reader ⇒
//       Header(~reader.readLine.split('%').lift(1))
//     }

//   def read(file: File): Future[Database] =
//     inputStream(file) { raw ⇒
//       gzip ⇒
//         try {
//           while (raw.read.toChar != '\n') {}
//           sbinary.Operations.read[Database](gzip)
//         }
//         catch {
//           case e: RuntimeException ⇒ throw new OutdatedDatabaseException(file.getName)
//         }
//     }

//   def write(file: File, db: Database) {
//     outputStream(file, db) { raw ⇒
//       gzip ⇒
//         raw write ("%" + db.header.toString + "%\n").getBytes
//         gzip write sbinary.Operations.toByteArray(db)
//     }
//   }
// }
