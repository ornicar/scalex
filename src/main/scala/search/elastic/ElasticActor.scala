package org.scalex
package search
package elastic

import akka.actor._
import akka.pattern.pipe
import play.api.libs.json._
import scalastic.elasticsearch.Indexer
import com.typesafe.config.Config

import actorApi._

private[search] final class ElasticActor(config: Config) extends Actor {

  var indexer: elasticsearch.Indexer = _

  override def preStart {
    println("[search] Instanciate indexer")
    indexer = Await.result(instanciateIndexer, 1 minute)
  }

  def receive = {

    case Search(request) ⇒ withEs { es ⇒
      SearchResponse(request.in(indexName, typeName)(es))
    } pipeTo sender

    // case Count(request) ⇒ withEs { es ⇒
    //   CountResponse(request.in(indexName, typeName)(es))
    // } pipeTo sender

    // case RebuildAll ⇒ {
    //   self ! Clear
    //   indexQuery(Json.obj()) pipeTo sender
    // }

    // case Optimize ⇒ withEs {
    //   _.optimize(Seq(indexName))
    // }

    // case InsertOne(id, doc) ⇒ withEs {
    //   _.index(indexName, typeName, id, Json stringify doc)
    // }

    // case InsertMany(list) ⇒ withEs { es ⇒
    //   es bulk {
    //     list map {
    //       case (id, doc) ⇒ es.index_prepare(indexName, typeName, id, Json stringify doc).request
    //     }
    //   }
    // }

    // case RemoveOne(id) ⇒ withEs {
    //   _.delete(indexName, typeName, id)
    // }

    // case RemoveQuery(query) ⇒ withEs {
    //   _.deleteByQuery(Seq(indexName), Seq(typeName), query)
    // }

    // case Clear ⇒ withEs { es ⇒
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

  private def instanciateIndexer = Future {
    elasticsearch.Indexer.transport(
      settings = Map("cluster.name" -> config.getString("cluster")),
      host = config getString "host",
      ports = Seq(config getString "port"))
  } andThen {
    case Success(indexer) ⇒
      println("[search] Start indexer")
      indexer.start
      println("[search] Indexer is running")
  }

  private def withEs[A](f: Indexer ⇒ A): Fu[A] = indexer map f
}

