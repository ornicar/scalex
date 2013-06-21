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

import makeTimeout.veryLarge
import model.Database

private[search] final class TextActor(database: Database, config: Config) extends Actor {

  var indexer: ActorRef = _

  override def preStart {
    indexer = context.actorOf(Props(
      new elastic.ElasticActor(config getConfig "elasticsearch")
    ), name = "elastic")
    // indexer ! elastic.api.Clear(Mapping.jsonMapping)
    val populator = new Populator(indexer)
    populator(database)
    Await.ready(indexer ? elastic.api.AwaitReady , 10 minutes)
    println("Text search ready!")
  }

  def receive = {

    case q: query.TextQuery ⇒
      indexer ? (Query search q) mapTo manifest[SearchResponse] map toResults pipeTo sender
  }

  private def toResults(response: SearchResponse): Results = {
    // println(response)
    Nil
  }
}
