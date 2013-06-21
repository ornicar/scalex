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
      typeNames = ???,//ScopeResolver(scope),
      from = (page - 1) * perPage,
      size = perPage)
  }

  def count = ??? // elastic.api.Count(makeQuery, Selector(q.scope)

  // private def tokens = tokens

  private def makeQuery = q.tokens.toNel.fold[BaseQueryBuilder](matchAllQuery) {
    _.foldLeft(boolQuery) {
      case (query, token) ⇒ query must {
        multiMatchQuery(token, fields.name)
      }
    }
  }

  // private def makeFilters = {
  //   (q.scope.include.toList map { termFilter(fields.project, _) })
  // }.toNel map { fs ⇒ andFilter(fs.list: _*) }
}

private[text] object Query {

  def search(q: TextQuery) = new Query(q).search

  def count(q: TextQuery) = new Query(q).count
}
