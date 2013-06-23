package org.scalex
package search
package text

import org.elasticsearch.index.query._, QueryBuilders._, FilterBuilders._

import query.{ TextQuery, Pagination }

private[text] final class Query(q: TextQuery) {

  import Mapping.f

  def search = new {
    def in(selector: Selector) = q match {
      case TextQuery(tokens, scope, Pagination(page, perPage)) ⇒ elastic.api.Search(
        query = makeQuery,
        typeNames = selector(scope) map (_.id),
        from = (page - 1) * perPage,
        size = perPage).pp
    }
  }

  def count = new {
    def in(selector: Selector) = elastic.api.Count(
      makeQuery, 
      selector(q.scope) map (_.id))
  }

  private def makeQuery = q.tokens.toNel.fold[BaseQueryBuilder](matchAllQuery) {
    _.foldLeft(boolQuery) {
      case (query, token) ⇒ query must {
        multiMatchQuery(token, f.name, f.memberEntity)
      }
    }
  }
}

private[text] object Query {

  def search(q: TextQuery) = new Query(q).search

  def count(q: TextQuery) = new Query(q).count
}
