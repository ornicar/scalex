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

  private val bulkSize = 2000

  def apply(repository: ActorRef, selector: Selector)(indexer: ActorRef): Future[Unit] = {

    def index(project: Project): Future[Unit] = {
      println("[%s] Indexing documents" format project)
      repository ? storage.api.GetSeed(project) mapTo manifest[Option[Seed]] flatMap {
        _.fold[Future[Any]](Future failed badArg("Can't find seed of " + project)) { seed ⇒
          indexer ! elastic.api.Clear(seed.project.id, Mapping.jsonMapping)
          Extractor fromSeed seed grouped bulkSize foreach { docs ⇒
            indexer ! elastic.api.IndexMany(seed.project.id, docs map Mapping.write)
          }
          indexer ? elastic.api.Optimize
        }
      }
    } void

    def isIndexed(project: Project): Future[Boolean] = {
      indexer ? Query.count(query.TextQuery(
        tokens = Nil,
        scope = query.Scope() + project.name,
        pagination = query.Pagination(1, Int.MaxValue)
      )).in(selector)
    } mapTo manifest[Int] map (0!=)

    (Future.traverse(selector.all) { s ⇒
      isIndexed(s) flatMap { indexed ⇒
        if (indexed) Future successful ()
        else index(s)
      }
    }).void
  }
}
