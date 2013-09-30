package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.pattern.{ ask, pipe }
import com.sksamuel.elastic4s.{ ElasticDsl ⇒ ES }
import com.typesafe.config.Config
import org.elasticsearch.action.search.SearchResponse
import play.api.libs.json._

import makeTimeout.veryLarge
import model.{ Database, Project }

private[search] final class TextActor(config: Config) extends Actor {

  private var es: ActorRef = _
  private var repository: ActorRef = _
  private var selector: Selector = _

  private val indexName = config getString "elastic.index"

  override def preStart {
    repository = context.actorOf(Props(
      new storage.Repository(config getConfig "repository")
    ), name = "repository")
    es = context.actorOf(Props(
      new elastic.ElasticActor(
        config = config getConfig "elastic",
        indexName = indexName,
        indexSettings = Index.settings)
    ), name = "elastic")
    selector = Await.result(
      repository ? storage.api.GetProjects mapTo manifest[List[Project]] map Selector,
      1 minute)
    Await.result(
      Populator(repository, selector)(es),
      10 minutes)
  }

  def receive = {

    case q: text.Query ⇒ {
      val area = selector(q.scope)
      es ? {
        ES.search(area.map(indexPrefix + _): _*) query q.definition 
      } mapTo manifest[SearchResponse] map result.ElasticToResult(q, area) pipeTo sender
    }
  }

  private val indexPrefix = indexName + "/"
}
