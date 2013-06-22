package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }

import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }

import document.Extractor
import makeTimeout.large
import model.{ Database, Project, Seed }

private[text] object Populator extends scalaz.NonEmptyListFunctions {

  def apply(database: Database)(indexer: ActorRef): Future[Unit] = {

    def fromSeed(seed: Seed): Future[Unit] = {
      println("[%s] Generate documents".format(seed))
      val documents = Extractor fromSeed seed
      println("[%s] Index %d documents".format(seed, documents.size))
      indexer ! elastic.api.Clear(seed.project.id, Mapping.jsonMapping)
      documents grouped 1000 foreach { docs ⇒
        indexer ! elastic.api.IndexMany(seed.project.id, docs map Mapping.write)
      }

      (indexer ? elastic.api.Optimize).void
    }

    def isIndexed(seed: Seed): Future[Boolean] = {
      indexer ? Query.count(query.TextQuery(
        tokens = Nil,
        scope = query.Scope() + seed.project.name,
        pagination = query.Pagination(1, Int.MaxValue)
      )).in(selector)
    } mapTo manifest[Int] map (0!=)

    def selector = Selector(database.projects)

    (Future.traverse(database.seeds) { s ⇒
      isIndexed(s) flatMap { indexed ⇒
        if (indexed) Future successful ()
        else fromSeed(s)
      }
    }).void
  }
}
