package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.Future

import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }

import document.ModelToDocument
import model.{ Database, Project, Seed }
import storage.Repository

private[text] object Populator extends scalaz.NonEmptyListFunctions {

  private val bulkSize = 2000

  def apply(repository: ActorRef, selector: Selector)(es: ActorRef, textActor: ActorRef): Fu[Unit] = {

    def index(project: Project): Fu[Unit] = {
      println(s"[$project] Indexing documents")
      import makeTimeout.veryLarge
      repository ? Repository.GetSeed(project) mapTo manifest[Option[Seed]] flatMap {
        _.fold(fufail[Any](badArg("Can't find seed of " + project))) { seed ⇒
          println(s"[$seed] ${seed.describe}")
          es ! elastic.api.Clear(seed.project.id, Index.mapping)
          ModelToDocument fromSeed seed grouped bulkSize foreach { docs ⇒
            println(s"Indexing ${docs.size} douments")
            es ! elastic.api.IndexMany(seed.project.id, docs map DocumentToElastic.apply)
          }
          println(s"Optimizing")
          es ? elastic.api.Optimize
        }
      }
    } void

    def isIndexed(project: Project): Fu[Boolean] = {
      import makeTimeout.short
      textActor ? Count(tokens = Nil, scope = query.Scope() + project.name)
    } mapTo manifest[Int] map (0!=)

    (Future.traverse(selector.all) { p ⇒
      isIndexed(p) flatMap { indexed ⇒
        if (indexed) fuccess()
        else index(p)
      }
    }).void
  }
}
