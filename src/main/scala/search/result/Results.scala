package org.scalex
package search
package result

import scala.concurrent.duration._

import play.api.libs.json._

import model.Project
import query.Query

case class Results(
    paginator: Paginator[Result],
    duration: Duration,
    query: Query,
    area: List[Project]) {

  override def toString = "Found %d results in %d ms.\n%s".format(
    paginator.nbResults,
    duration.toMillis,
    paginator.results.take(8).zipWithIndex map {
      case (result, i) ⇒ "%d. %s\n".format(i + 1, result)
    } mkString "\n"
  )

  // def toJson: JsObject = Json.obj(
  // )
}

object Results {

  def apply(
    query: Query,
    area: List[Project],
    millis: Int,
    rs: List[Result],
    nbRs: Int): Results = Results(
    paginator = new Paginator(
      results = rs,
      nbResults = nbRs,
      currentPage = query.pagination.currentPage,
      maxPerPage = query.pagination.maxPerPage),
    duration = millis.millis,
    query = query,
    area = area)
}
