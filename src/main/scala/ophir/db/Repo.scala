package ophir.db

import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._

abstract class Repo[M <: CaseClass] {

  val collection: MongoCollection

  val mapper: Grater[M]

  def index() = {}

  def drop() {
    collection remove MongoDBObject.empty
    collection.dropIndexes
  }

  def save(obj: M) { collection insert serialize(obj) }

  def save(objs: List[M]) { collection insert (objs map serialize) }

  def remove(obj: M) { collection remove serialize(obj) }

  def findAll: Iterator[M] = collection.find map unserialize

  def find(query: MongoDBObject): Iterator[M] =
     collection find query map unserialize

  def findBy[A <: String, B <: Any](elems: (A, B)*): Iterator[M] =
     find(MongoDBObject(elems.toList))

  def unserialize(dbo: DBObject): M = mapper asObject dbo

  def serialize(obj: M): DBObject = mapper asDBObject obj

  def count: Long = collection.count
}
