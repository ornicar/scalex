package org.scalex
package search
package result

import scala.concurrent.duration._

import org.elasticsearch.action.search.SearchResponse
import play.api.libs.json._

import model.Project

private[search] object ElasticToResult {

  def apply(q: query.Query, area: List[Project])(response: SearchResponse) = Results(
    paginator = new Paginator(
      results = response.getHits.hits.toList map { hit ⇒
        text.ElasticToDocument(
          projectName = hit.getType,
          id = hit.id,
          json = Json parse hit.sourceAsString
        ) map { result.Result(_, math round hit.score) }
      } flatten,
      nbResults = response.getHits.totalHits.toInt,
      currentPage = q.pagination.currentPage,
      maxPerPage = q.pagination.maxPerPage),
    duration = response.getTookInMillis.toInt.millis,
    query = q,
    area = area)
}
