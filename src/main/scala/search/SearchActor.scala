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

import makeTimeout.veryLarge

private[search] final class SearchActor(config: Config) extends Actor {

  private var textualEngine: ActorRef = _

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: ActorInitializationException ⇒ Escalate
      case _: Exception                    ⇒ Restart
    }

  override def preStart {
    textualEngine = context.actorOf(Props(
      new text.TextActor(config)
    ), name = "text")
  }

  def receive = {

    case expression: String ⇒ apply(expression) pipeTo sender
  }

  private def apply(expression: String): Future[Try[Results]] =
    query.Raw(expression, 1, 10).analyze match {
      case Success(query) ⇒ apply(query) map { Success(_) }
      case Failure(err)   ⇒ Future successful Failure(err)
    }

  private def apply(q: query.Query): Future[Results] = q match {
    case q: query.TextQuery ⇒ textualEngine ? q mapTo manifest[Results]
    case _                  ⇒ ???
  }
}
