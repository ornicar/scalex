package org.scalex
package search
package text

import org.elasticsearch.index.query._, QueryBuilders._, FilterBuilders._

import query.{ TextQuery, Pagination }

private[text] final class Query(q: TextQuery) {

  import Mapping.fields

  def search = q match {
    case TextQuery(tokens, scope, Pagination(page, perPage)) ⇒ elastic.api.Search(
      query = makeQuery,
      filter = makeFilters,
      from = (page - 1) * perPage,
      size = perPage)
  }

  def count = elastic.api.Count(makeQuery, makeFilters)

  // private def tokens = tokens

  private def makeQuery = q.tokens.list.foldLeft(boolQuery) {
    case (query, token) ⇒ query must {
      multiMatchQuery(token, fields.name)
    }
  }

  private def makeFilters = List(
    q.scope.include.nonEmpty option q.scope.include map { 
      termFilter(fields.project, _)
    }
    // userSearch map { termFilter(fields.author, _) },
    // !staff option termFilter(fields.staff, false),
    // !troll option termFilter(fields.troll, false)
  ).flatten.toNel map { fs ⇒ andFilter(fs.list: _*) }
}

private[text] object Query {

  def search(q: TextQuery) = new Query(q).search

  def count(q: TextQuery) = new Query(q).count
}
