package org.scalex
package search

import scala.concurrent.Future
import scala.util.{ Try, Success, Failure }

import akka.actor.{ ActorRef, Props }
import akka.pattern.ask

final class Search(env: Env) {

  private implicit val timeout = makeTimeout.veryLarge

  private val actor: ActorRef = env.system.actorOf(Props(
    new SearchActor(env.config)
  ))

  def apply(expression: String): Future[Try[Results]] =
    actor ? expression mapTo manifest[Try[Results]]
}

object Search {

  def default = new Search(Env.default)
}
