package scalex.db

import scalex.model.Def
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._

object DefRepo extends SalatDAO[Def, ObjectId](collection = MongoConnection()("scalex")("def")) {

  def batchInsert(objs: List[Def]) { collection insert (objs map _grater.asDBObject) }

  def findByTokens(tokens: List[String]): Iterator[Def] =
    find(MongoDBObject("$and" -> tokensToRegexes(tokens)))

  def findBySig(sig: String): Iterator[Def] =
    find(MongoDBObject("sigTokens" -> sigToRegex(sig)))

  def findByTokensAndSig(tokens: List[String], sig: String): Iterator[Def] =
    find(MongoDBObject("$and" -> tokensToRegexes(tokens), "sigTokens" -> sigToRegex(sig)))

  private def tokensToRegexes(tokens: List[String]) =
    tokens map (token => MongoDBObject("tokens" -> ("^%s" format token.toLowerCase).r))

  private def sigToRegex(sig: String) =
    ("^" + "(){}+*.[]-^$".toList.foldLeft(sig.toLowerCase)((a, b) => a.replace(b.toString, "\\" + b))).r

  def findAll: Iterator[Def] = find(MongoDBObject())

  def index() {
    collection.ensureIndex(MongoDBObject("tokens" -> 1))
    collection.ensureIndex(MongoDBObject("sigTokens" -> 1))
  }

  def drop() {
    collection remove MongoDBObject()
    collection.dropIndexes
  }
}
