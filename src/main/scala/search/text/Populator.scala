package org.scalex
package search
package text

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }

import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }

import document.Extractor
import model.{ Database, Project }

private[text] final class Populator(indexer: ActorRef) extends scalaz.NonEmptyListFunctions {

  def apply(database: Database) {

    database.projects filterNot isIndexed foreach populateProject 
  }

  private def isIndexed(project: Project): Boolean = {
    import makeTimeout.large
    println("populator count")
    Await.result(indexer ? Query.count(query.TextQuery(
      tokens = nel(project.name, Nil),
      scope = query.Scope(include = Set(project.name)),
      pagination = query.Pagination(1, Int.MaxValue)
    )) mapTo manifest[Int], 5 second).pp > 0
  }

  private def populateProject(project: Project) {

    Extractor(project) grouped 1000 foreach { docs ⇒
      indexer ! elastic.api.IndexMany(docs map {
        Mapping.from(project.name, _)
      })
    }

    indexer ! elastic.api.Optimize
  }
}
