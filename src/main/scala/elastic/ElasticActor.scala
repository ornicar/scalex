package org.scalex
package elastic

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import com.typesafe.config.Config
import scalastic.elasticsearch.Indexer
import org.elasticsearch.action.search.SearchResponse

import api._

private[scalex] final class ElasticActor(config: Config) extends Actor {

  var indexer: Indexer = _

  override def preStart {
    println("[search] Instanciate indexer")
    indexer = Await.result(instanciateIndexer, 1 minute)
  }

  def receive = {

    case req: Request.Search ⇒
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

  private def instanciateIndexer = Future {
    Indexer.transport(
      settings = Map("cluster.name" -> config.getString("cluster")),
      host = config getString "host",
      ports = Seq(config getInt "port"))
  } andThen {
    case Success(indexer) ⇒
      println("[search] Start indexer")
      indexer.start
      println("[search] Indexer is running")
  }
}
