package scalex

import db.{ IndexRepo, DefRepo }
import search.Engine
import dump.Dumper

import com.mongodb.casbah.MongoConnection
import com.typesafe.config._

trait Env {

  val config: Config


  lazy val engine = new Engine(indexRepo.read, defRepo.byIds)

  lazy val indexRepo = new IndexRepo(
    config getString "scalex.index"
  )

  lazy val defRepo = new DefRepo(
    mongoDatabase(config getString "scalex.mongo.def_collection")
  )

  def dumper = new Dumper(defRepo)

  private lazy val mongoConnection = MongoConnection(
    config getString "scalex.mongo.host",
    config getInt "scalex.mongo.port"
  )

  private lazy val mongoDatabase = mongoConnection(
    config getString "scalex.mongo.dbName"
  )
}

object Env extends EnvBuilder {

  def apply(overrides: String = "") = {
    val c = makeConfig(overrides)
    // early config check
    c getString "scalex.index"
    new Env { val config = c }
  }
}

trait EnvBuilder {

  import java.io.File

  def makeConfig(sources: String*) = sources.foldLeft(ConfigFactory.defaultOverrides) {
    case (config, source) if source isEmpty ⇒ config
    case (config, source) if source contains '=' ⇒
      config.withFallback(ConfigFactory parseString source)
    case (config, source) ⇒
      config.withFallback(ConfigFactory parseFile (new File(source)))
  }
}
