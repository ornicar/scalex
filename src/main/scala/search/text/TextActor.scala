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
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.search.SearchResponse
import play.api.libs.json._

import model.{ Database, Project }

private[search] final class TextActor(config: Config)
    extends FSM[TextActor.State, TextActor.Data] {

  import TextActor._

  private var es: ActorRef = _
  private var repository: ActorRef = _
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
  }

  startWith(Booting, Pile(Vector.empty))
  self ! MakeSelector

  when(Booting) {

    case Event(MakeSelector, _) ⇒ {
      import makeTimeout.short
      repository ? storage.api.GetProjects mapTo
        manifest[List[Project]] map Selector pipeTo self
      stay
    }

    case Event(selector: Selector, pile: Pile) ⇒ {
      Populator(repository, selector)(es, self) onComplete self.!
      goto(Populating) using PileWith(pile, selector)
    }

    case Event(query: Query, pile: Pile) ⇒
      stay using (pile + Job(query, sender))
  }

  when(Populating) {

    case Event(q: Count, PileWith(_, selector)) ⇒ {
      count(selector)(q)
      stay
    }

    case Event(Failure(err), _) ⇒ throw err

    case Event(Success(_), PileWith(Pile(jobs), selector)) ⇒ {
      jobs foreach self.!
      context.parent ! Ready
      goto(Ready) using With(selector)
    }

    case Event(query: Query, pileWith: PileWith) ⇒
      stay using (pileWith + Job(query, sender))
  }

  when(Ready) {

    case Event(Job(query, from), _) ⇒ {
      import makeTimeout.short
      self ? query pipeTo from
      stay
    }

    case Event(q: Query, With(selector)) ⇒ {
      val area = selector(q.scope)
      val types = area map (_.id)
      import makeTimeout.short
      es ? {
        ES.search.in(indexName).types(types: _*) query q.definition
      } mapTo
        manifest[SearchResponse] map
        result.ElasticToResult(q, area) pipeTo sender
      stay
    }

    case Event(q: Count, With(selector)) ⇒ {
      count(selector)(q)
      stay
    }
  }

  private def count(selector: Selector)(q: Count) {
    val types = selector(q.scope) map (_.id)
    import makeTimeout.short
    (es ? {
      ES.count.from(indexName).types(types) query q.definition
    }) mapTo manifest[CountResponse] map (_.getCount.toInt) pipeTo sender
  }

  private val indexPrefix = indexName + "/"
}

private[search] object TextActor {

  case object MakeSelector
  case object Populated

  sealed trait State
  case object Booting extends State
  case object Populating extends State
  case object Ready extends State

  sealed trait Data
  case class Job(query: Query, from: ActorRef)
  case class Pile(jobs: Vector[Job]) extends Data {
    def +(job: Job) = copy(jobs :+ job)
  }
  case class With(selector: Selector) extends Data
  case class PileWith(pile: Pile, selector: Selector) extends Data {
    def +(job: Job) = copy(pile = pile + job)
  }
}
