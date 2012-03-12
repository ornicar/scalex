package scalex
package db

import model.Def

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoCollection
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._

class DefRepo(collection: MongoCollection) extends SalatDAO[Def, ObjectId](collection) {

  def batchInsert(objs: List[Def]) { collection insert (objs map _grater.asDBObject) }

  def byIds(ids: Seq[String]): List[Def] = {
    val defs = find("_id" $in ids) toList
    val sorted = ids map { id => defs.find(_.id == id) }
    sorted.flatten.toList
  }

  def removePack(pack: String) {
    collection remove MongoDBObject("pack" -> pack)
  }

  def findAll: List[Def] = find(MongoDBObject()).toList

  def findByDeclaration(dec: String) = findOne(MongoDBObject(
    "declaration" -> dec
  ))
}
