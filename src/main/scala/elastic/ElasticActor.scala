package org.scalex
package elastic

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import com.typesafe.config.Config
import org.elasticsearch.action.search.SearchResponse
import play.api.libs.json.{ Json, JsObject }
import scalastic.elasticsearch.Indexer

import api._
import util.Timer._

private[scalex] final class ElasticActor(config: Config) extends Actor {

  private val indexName = config getString "index"

  var indexer: Indexer = _

  override def preStart {
    indexer = instanciateIndexer
  }

  override def postStop {
    Option(indexer) foreach (_.stop)
    println("[search] Indexer is stopped")
  }

  def receive = akka.event.LoggingReceive {

    case api.Clear(typeName, mapping) ⇒ Await.ready(Future {
      indexer.deleteByQuery(Seq(indexName), Seq(typeName))
      indexer.deleteMapping(indexName :: Nil, typeName.some)
      indexer.putMapping(indexName, typeName, Json stringify mapping)
      indexer.refresh()
    }, 3 second)

    case api.Optimize ⇒ sender ! {
      // wrapAndMonitor("ES optimize") {
      indexer.refresh(Seq(indexName))
      indexer.optimize(Seq(indexName))
      // }
    }

    case api.AwaitReady ⇒ sender ! indexer.waitTillActive(Seq(indexName))

    case api.IndexMany(typeName, docs) ⇒
      // wrapAndMonitor("ES index %d docs" format docs.size) {
      indexer bulk {
        docs map {
          case (id, doc) ⇒
            indexer.index_prepare(
              indexName,
              typeName,
              id,
              Json stringify doc
            ).request
        }
        // }
      }

    case req: api.Request[_] ⇒ sender ! req.in(indexName)(indexer)
  }

  private def instanciateIndexer = {
    println("[search] Start indexer")
    val i = Indexer.transport(
      settings = Map("cluster.name" -> config.getString("cluster")),
      host = config getString "host",
      ports = Seq(config getInt "port"))
    i.start
    try {
      i.createIndex(indexName, settings = Map.empty)
    }
    catch {
      case e: org.elasticsearch.indices.IndexAlreadyExistsException ⇒
    }
    println("[search] Indexer is running")
    i
  }
}
