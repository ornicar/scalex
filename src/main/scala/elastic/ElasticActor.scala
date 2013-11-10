package org.scalex
package elastic

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.pattern.{ ask, pipe }
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.{ ElasticDsl ⇒ ES }
import com.typesafe.config.Config
import org.elasticsearch.action.search.SearchResponse
import play.api.libs.json.{ Json, JsObject }

import api._
import util.Timer._

private[scalex] final class ElasticActor(
    config: Config,
    indexName: String,
    indexSettings: Map[String, String]) extends Actor {

  var client: ElasticClient = _

  override def preStart {
    client = instanciateElasticClient
  }

  override def postStop {
    println("[search] Stop elastic client")
    Option(client) foreach { _.close() }
  }

  def receive = {

    case api.Clear(typeName, mapping) ⇒ Await.ready((Future {
      try {
        client execute { ES.delete from s"$indexName/typeName" where ES.matchall }
      }
      catch {
        case e: org.elasticsearch.indices.TypeMissingException ⇒
      }
      // TODO set type mappings
      // client execute {
      //   ES.create index indexName mappings (typeName as mapping)
      // }
    }) recover { case e ⇒ println("[elastic] clear: " + e) }, 3 second)

    case api.Optimize ⇒ client execute { ES optimize indexName } pipeTo sender

    case api.IndexMany(typeName, docs) ⇒ client bulk {
      (docs map {
        case (id, source) ⇒ ES.index into s"$indexName/$typeName" id id source source
      }): _*
    }

    case search: ES.SearchDefinition ⇒ execute(client execute search, sender)

    case count: ES.CountDefinition   ⇒ execute(client execute count, sender)
  }

  private def execute[A](action: Future[A], replyTo: ActorRef) {
    action onComplete {
      case Success(response)  ⇒ replyTo ! response
      case Failure(exception) ⇒ throw exception
    }
  }

  private def instanciateElasticClient = {
    println("[search] Start elastic client")
    ElasticClient.remote(config getString "host", config getInt "port") ~ { c ⇒
      try {
        // TODO what about indexSettings??
        c execute { ES.create index indexName }
      }
      catch {
        case e: org.elasticsearch.indices.IndexAlreadyExistsException ⇒
      }
      println("[search] Elastic client running")
    }
  }
}
