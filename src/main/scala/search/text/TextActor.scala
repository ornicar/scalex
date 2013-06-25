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
import model.{ Database, Project }

private[search] final class TextActor(config: Config) extends Actor {

  private var indexer: ActorRef = _
  private var repository: ActorRef = _
  private var selector: Selector = _

  override def preStart {
    repository = context.actorOf(Props(
      new storage.Repository(config getConfig "repository")
    ), name = "repository")
    indexer = context.actorOf(Props(
      new elastic.ElasticActor(config getConfig "elastic")
    ), name = "elastic")
    selector = Await.result(
      repository ? storage.api.GetProjects mapTo manifest[List[Project]] map Selector,
      1 minute)
    Await.result(
      Populator(repository, selector)(indexer),
      10 minutes)
  }

  def receive = {

    case q: query.TextQuery ⇒ {
      val area = selector(q.scope)
      indexer ? (Query search q in area) mapTo
        manifest[SearchResponse] map
        toResults(q, area) pipeTo sender
    }
  }

  private def toResults(q: query.Query, area: List[Project])(response: SearchResponse) =
    result.Results(
      query = q,
      area = area,
      millis = response.getTookInMillis.toInt,
      rs = response.getHits.hits.toList map { hit ⇒
        ElasticToDocument(
          projectName = hit.getType,
          id = hit.id,
          json = Json parse hit.sourceAsString
        ) map { result.Result(_, math round hit.score) }
      } flatten,
      nbRs = response.getHits.totalHits.toInt)
}
