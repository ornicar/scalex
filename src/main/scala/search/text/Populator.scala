package org.scalex
package search
package text

import akka.actor.ActorRef

import document.Extractor
import model.Database

private[text] object Populator {

  def apply(indexer: ActorRef, database: Database) {

    println("Start populator")

    indexer ! elastic.api.Clear(Mapping.jsonMapping)

    Extractor flat database grouped 1000 foreach { docs ⇒
      indexer ! elastic.api.IndexMany(docs map {
        case (project, doc) ⇒ Mapping.from(project, doc)
      })
    }
  }
}
