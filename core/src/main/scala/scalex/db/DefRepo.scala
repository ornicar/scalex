package scalex.db

import scalex.model.Def
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.github.ornicar.paginator.SalatAdapter

object DefRepo extends SalatDAO[Def, ObjectId](collection = MongoConnection()("scalex")("def")) {

  def paginatorAdapter(query: MongoDBObject) = SalatAdapter[Def, ObjectId](this, query)

  def batchInsert(objs: List[Def]) { collection insert (objs map _grater.asDBObject) }

  def queryByTokens(tokens: List[String]): MongoDBObject =
    MongoDBObject("$and" -> tokensToRegexes(tokens))

  def queryBySig(sig: String): MongoDBObject =
    MongoDBObject("sigTokens" -> sig.toLowerCase)

  def queryByTokensAndSig(tokens: List[String], sig: String): MongoDBObject =
    MongoDBObject("$and" -> tokensToRegexes(tokens), "sigTokens" -> sig.toLowerCase)

  private def tokensToRegexes(tokens: List[String]): List[DBObject] = {
    val quoteRegex = """()[]{}\+-?^$.~""".toList.map("""\""" + _).mkString("(", "|", ")").r
    def escapeToken(token: String) = quoteRegex.replaceAllIn(token, m => """\\""" + m.group(0))
    tokens map (token => MongoDBObject("tokens" -> ("^%s" format escapeToken(token.toLowerCase)).r))
  }

  def findAll: Iterator[Def] = find(MongoDBObject())

  def index() {
    collection.ensureIndex(MongoDBObject("tokens" -> 1))
    collection.ensureIndex(MongoDBObject("sigTokens" -> 1))
    collection.ensureIndex(MongoDBObject("pack" -> 1))
  }

  def removePack(pack: String) {
    collection remove MongoDBObject("pack" -> pack)
  }
}
