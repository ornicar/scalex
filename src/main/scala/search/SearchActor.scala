package org.scalex
package search

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.pattern.{ ask, pipe }
import com.typesafe.config.Config
import scalaz.{ \/, -\/, \/- }

import text.TextActor

private[search] final class SearchActor(config: Config)
    extends FSM[SearchActor.State, SearchActor.Data] {

  import SearchActor._

  private var textualEngine: ActorRef = _

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: ActorInitializationException ⇒ Escalate
      case _: InvalidDatabaseException     ⇒ Stop
      case _: Exception                    ⇒ Restart
    }

  override def preStart {
    textualEngine = context.actorOf(Props(
      new TextActor(config)
    ), name = "text")
    context watch textualEngine
  }

  startWith(Booting, Pile(Vector.empty))

  when(Booting) {

    case Event(q: String, pile: Pile) ⇒
      stay using (pile + Job(q, sender))

    case Event(TextActor.Ready, Pile(jobs)) ⇒ {
      jobs foreach self.!
      goto(Ready) using NoData
    }

    // something went wrong and the child actor terminated
    // abort all jobs then die
    case Event(Terminated(_), Pile(jobs)) ⇒ {
      jobs foreach {
        case Job(_, from) ⇒ from ! Status.Failure(new SearchFailureException)
      }
      self ! PoisonPill
      stay
    }
  }

  when(Ready) {

    case Event(Job(q, from), _) ⇒ {
      import makeTimeout.short
      self ? q pipeTo from
      stay
    }

    case Event(q: String, _) ⇒ {
      self forward query.Raw(q)
      stay
    }

    case Event(q: query.Raw, _) ⇒ {
      import makeTimeout.short
      q.analyze.fold(
        err ⇒ sender ! -\/(err),
        query ⇒ textualEngine ? query map { \/-(_) } pipeTo sender
      )
      stay
    }
  }
}

private[search] object SearchActor {

  sealed trait State
  case object Booting extends State
  case object Ready extends State

  sealed trait Data
  case object NoData extends Data
  case class Job(query: String, from: ActorRef)
  case class Pile(jobs: Vector[Job]) extends Data {
    def +(job: Job) = copy(jobs :+ job)
  }
}
