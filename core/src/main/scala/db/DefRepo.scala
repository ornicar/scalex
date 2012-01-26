package scalex
package db

import model.Def
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._

object DefRepo extends SalatDAO[Def, ObjectId](collection = MongoConnection()("scalex")("def")) {

  def batchInsert(objs: List[Def]) { collection insert (objs map _grater.asDBObject) }

  def byIds(ids: Seq[String]): List[Def] = find("_id" $in ids).toList

  def removePack(pack: String) {
    collection remove MongoDBObject("pack" -> pack)
  }
}
