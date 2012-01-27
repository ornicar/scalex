package scalex

import db.{ IndexRepo, DefRepo }
import search.Engine
import dump.Dumper

import com.mongodb.casbah.MongoConnection

class Env(config: Map[String, String] = Map.empty) {

  lazy val engine = new Engine(
    indexes = indexRepo.read,
    idsToDefs = defRepo.byIds
  )

  lazy val indexRepo = new IndexRepo(
    conf("index.file") | "index.dat"
  )

  lazy val defRepo = new DefRepo(
    mongoDatabase(conf("mongo.collection.def") | "def")
  )

  def dumper = new Dumper(defRepo)

  private lazy val mongoConnection = MongoConnection(
    conf("mongo.host") | "localhost",
    conf("mongo.port") some (_.toInt) none 27017
  )

  private lazy val mongoDatabase = mongoConnection(
    conf("mongo.dbname") | "scalex"
  )

  def conf(key: String) = config get key
}
