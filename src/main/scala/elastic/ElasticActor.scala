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

  var indexer: Indexer = _

  override def preStart {
    indexer = instanciateIndexer
  }

  override def postStop {
    Option(indexer) foreach (_.stop)
    println("[search] Indexer is stopped")
  }

  def receive = {

    case api.Clear(mapping) ⇒ {
      Await.ready(Future {
        indexer.deleteByQuery(Seq(indexName), Seq(typeName))
        indexer.deleteMapping(indexName :: Nil, typeName.some)
        indexer.putMapping(indexName, typeName, Json stringify mapping)
        indexer.refresh()
      }, 3 second)
    }

    case api.Optimize ⇒ {
      // wrapAndMonitor("ES optimize") {
        indexer.refresh(Seq(indexName))
        indexer.optimize(Seq(indexName))
      // }
    }

    case api.AwaitReady ⇒ sender ! indexer.waitTillActive(Seq(indexName))

    case api.IndexMany(docs) ⇒ 
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

    case req: api.Request[_] ⇒
      sender ! req.in(indexName, typeName)(indexer)

    // case Count(request) ⇒ withIndexer { es ⇒
    //   CountResponse(request.in(indexName, typeName)(es))
    // } pipeTo sender

    // case RebuildAll ⇒ {
    //   self ! Clear
    //   indexQuery(Json.obj()) pipeTo sender
    // }

    // case Optimize ⇒ withIndexer {
    //   _.optimize(Seq(indexName))
    // }

    // case InsertOne(id, doc) ⇒ withIndexer {
    //   _.index(indexName, typeName, id, Json stringify doc)
    // }

    // case InsertMany(list) ⇒ withIndexer { es ⇒
    //   es bulk {
    //     list map {
    //       case (id, doc) ⇒ es.index_prepare(indexName, typeName, id, Json stringify doc).request
    //     }
    //   }
    // }

    // case RemoveOne(id) ⇒ withIndexer {
    //   _.delete(indexName, typeName, id)
    // }

    // case RemoveQuery(query) ⇒ withIndexer {
    //   _.deleteByQuery(Seq(indexName), Seq(typeName), query)
    // }

    // case Clear ⇒ withIndexer { es ⇒
    //   try {
    //     es.createIndex(indexName, settings = Map())
    //   }
    //   catch {
    //     case e: org.elasticsearch.indices.IndexAlreadyExistsException ⇒
    //   }
    //   try {
    //     es.deleteByQuery(Seq(indexName), Seq(typeName))
    //     es.waitTillActive()
    //     es.deleteMapping(indexName :: Nil, typeName.some)
    //   }
    //   catch {
    //     case e: org.elasticsearch.indices.TypeMissingException ⇒
    //   }
    //   es.putMapping(indexName, typeName, Json stringify Json.obj(typeName -> mapping))
    //   es.refresh()
    // }
  }

  private val indexName = config getString "index"
  private val typeName = config getString "type"

  private def instanciateIndexer = {
    val i = Indexer.transport(
      settings = Map("cluster.name" -> config.getString("cluster")),
      host = config getString "host",
      ports = Seq(config getInt "port"))
    println("[search] Start indexer")
    i.start
    i.waitTillActive(Seq(indexName))
    // i.waitForYellowStatus(Seq(indexName))
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
