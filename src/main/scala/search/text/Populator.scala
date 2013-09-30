package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.Future

import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }

import document.ModelToDocument
import makeTimeout.veryLarge
import model.{ Database, Project, Seed }

private[text] object Populator extends scalaz.NonEmptyListFunctions {

  private val bulkSize = 2000

  def apply(repository: ActorRef, selector: Selector)(es: ActorRef): Fu[Unit] = {

    def index(project: Project): Fu[Unit] = {
      println("[%s] Indexing documents" format project)
      repository ? storage.api.GetSeed(project) mapTo manifest[Option[Seed]] flatMap {
        _.fold(fufail[Any](badArg("Can't find seed of " + project))) { seed ⇒
          es ! elastic.api.Clear(seed.project.id, Index.mapping)
          ModelToDocument fromSeed seed grouped bulkSize foreach { docs ⇒
            es ! elastic.api.IndexMany(seed.project.id, docs map DocumentToElastic.apply)
          }
          es ? elastic.api.Optimize
        }
      }
    } void

    def isIndexed(project: Project): Fu[Boolean] = {
      val q = text.Query(
        raw = "",
        tokens = Nil,
        scope = query.Scope() + project.name,
        pagination = query.Pagination(1, Int.MaxValue)
      )
      // TODO es ? Query.count(q).in(selector(q.scope))
      fufail("TODO")
    } mapTo manifest[Int] map (0!=)

    (Future.traverse(selector.all) { p ⇒
      isIndexed(p) flatMap { indexed ⇒
        if (indexed) fuccess()
        else index(p)
      }
    }).void
  }
}
