package scalex
package db

import index.Def
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._

object IndexRepo extends SalatDAO[Def, ObjectId](collection = MongoConnection()("scalex")("def")) {

  val keys = MongoDBObject(
    "_id" -> true,
    "name" -> true,
    "qualifiedName" -> true
  )

  def findAll: List[Def] = find(MongoDBObject(), keys).toList
}
