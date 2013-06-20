package org.scalex
package search
package elastic

import org.elasticsearch.action.search.{ SearchResponse ⇒ ESSR }
import org.elasticsearch.index.query.QueryBuilder

private[search] object api {

  case class Search(request: ElasticSearch.Request.Search)
  case class SearchResponse(res: ESSR)
  case class Count(request: ElasticSearch.Request.Count)
  case class CountResponse(res: Int)
}
