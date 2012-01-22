package scalex.db

import scalex.model.Def
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._

object DefRepo extends SalatDAO[Def, ObjectId](collection = MongoConnection()("scalex")("def")) {

  def batchInsert(objs: List[Def]) { collection insert (objs map _grater.asDBObject) }

  def findAll: List[Def] = find(MongoDBObject()).toList

  def removePack(pack: String) {
    collection remove MongoDBObject("pack" -> pack)
  }
}
