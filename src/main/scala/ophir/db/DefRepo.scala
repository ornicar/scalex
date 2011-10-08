package ophir.db

import ophir.model.Def
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._

object DefRepo extends SalatDAO[Def, ObjectId](collection = MongoConnection()("ophir")("def")) {

  def batchInsert(objs: List[Def]) { collection insert (objs map _grater.asDBObject) }

  def findByTokens(tokens: List[String]): Iterator[Def] = {
    val equalities = tokens map (token => MongoDBObject("tokens" -> token.toLowerCase))
    find(MongoDBObject("$and" -> equalities))
  }

  def findBySig(sig: String): Iterator[Def] =
    find(MongoDBObject("normalizedTypeSig" -> sig))

  def findAll: Iterator[Def] = find(MongoDBObject())

  def index() {
    collection.ensureIndex(MongoDBObject("tokens" -> 1))
    collection.ensureIndex(MongoDBObject("normalizedTypeSig" -> 1))
  }

  def drop() {
    collection remove MongoDBObject()
    collection.dropIndexes
  }
}
