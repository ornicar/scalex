package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.pattern.pipe
import makeTimeout.veryLarge
import scalastic.elasticsearch
import com.typesafe.config.Config

import model.Database

private[search] final class TextActor(database: Database, config: Config) extends Actor {

  var indexer: ActorRef = _

  override def preStart {
    indexer = context.actorOf(Props(
      new elastic.ElasticActor(config getConfig "elasticsearch")
    ))
  }

  def receive = {

    case TextQuery(tokens, scope) ⇒ ???
  }
}
