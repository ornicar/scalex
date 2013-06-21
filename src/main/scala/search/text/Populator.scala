package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }

import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }

import document.Extractor
import model.{ Database, Project, Seed }

private[text] final class Populator(indexer: ActorRef) extends scalaz.NonEmptyListFunctions {

  def apply(database: Database) {

    database.seeds filterNot isIndexed foreach fromSeed 
  }

  def fromSeed(seed: Seed) {
    println("[%s] Generate documents".format(seed))
    val documents = Extractor fromSeed seed
    println("[%s] Index %d documents".format(seed, documents.size))
    indexer ! elastic.api.Clear(seed.project.id, Mapping.jsonMapping)
    documents grouped 1000 foreach { docs ⇒
      indexer ! elastic.api.IndexMany(seed.project.id) docs map {
        Mapping.from(seed.project.id, _)
      }
    }

    indexer ! elastic.api.Optimize
  }

  private def isIndexed(seed: Seed): Boolean = {
    import makeTimeout.large
    println("populator count")
    Await.result(indexer ? Query.count(query.TextQuery(
      tokens = Nil,
      scope = query.Scope(include = Set(seed.project.name)),
      pagination = query.Pagination(1, Int.MaxValue)
    )) mapTo manifest[Int], 5 second).pp > 0
  }
}
