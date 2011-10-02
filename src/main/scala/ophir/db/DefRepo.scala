package ophir.db

import ophir.model.Def
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._

object DefRepo extends Repo[Def] {

  lazy val collection = MongoConnection("localhost")("ophir")("def")

  lazy val mapper = grater[Def]

  def findByTokens(tokens: List[String]): Iterator[Def] = {
    val equalities = tokens map (token => MongoDBObject("tokens" -> token.toLowerCase))
    find(MongoDBObject("$and" -> equalities))
  }

  override def index() {
    collection.ensureIndex(MongoDBObject("qualifiedName" -> 1), "unicity", true)
    collection.ensureIndex(MongoDBObject("tokens" -> 1))
  }
}
