package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.pattern.{ ask, pipe }
import com.typesafe.config.Config
import org.elasticsearch.action.search.SearchResponse
import play.api.libs.json._

import makeTimeout.veryLarge
import model.Database

private[search] final class TextActor(database: Database, config: Config) extends Actor {

  private val selector = Selector(database.projects)

  private var indexer: ActorRef = _

  override def preStart {
    indexer = context.actorOf(Props(
      new elastic.ElasticActor(config getConfig "elasticsearch")
    ), name = "elastic")
    Await.ready(indexer ? elastic.api.AwaitReady, 1 minute)
    Await.ready(Populator(database)(indexer), 10 minutes)
    println("Text search ready!")
  }

  def receive = {

    case q: query.TextQuery ⇒
      indexer ? (Query search q in selector) mapTo manifest[SearchResponse] map toResults pipeTo sender
  }

  private def toResults(response: SearchResponse): Results = {
    response.getHits.hits.toList map { hit ⇒
      println(hit.getType)
      println(math round hit.score)
      println(hit.id)
      println(Json parse hit.sourceAsString)
      // Result(???, math round hit)
    }
    Nil
  }
}
