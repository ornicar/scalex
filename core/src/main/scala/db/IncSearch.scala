package scalex
package db

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoCollection
import com.mongodb.WriteConcern

final class IncSearch(collection: MongoCollection) {

  // there will be more write than read,
  // so not sure the index makes sense
  // collection.ensureIndex(MongoDBObject("c" -> -1))

  def apply(q: String) {
    try {
      collection.update(
        MongoDBObject("_id" -> q),
        MongoDBObject("$inc" -> MongoDBObject("c" -> 1)),
        true,
        false,
        WriteConcern.NONE)
    }
    catch {
      case e ⇒ println("Fail to increment search " + q)
    }
  }
}
