package org.scalex
package search
package text

import org.elasticsearch.index.query._, QueryBuilders._, FilterBuilders._

import query.{ TextQuery, Pagination }
import model.Project

private[text] final class Query(q: TextQuery) {

  def search = new {
    def in(projects: List[Project]) = q match {
      case TextQuery(_, tokens, scope, Pagination(page, perPage)) ⇒ elastic.api.Search(
        query = makeQuery,
        typeNames = projects map (_.id),
        from = (page - 1) * perPage,
        size = perPage)
    }
  }

  def count = new {
    def in(projects: List[Project]) = 
      elastic.api.Count(makeQuery, projects map (_.id))
  }

  private def f = Index.fields

  private def makeQuery = q.tokens.toNel.fold[BaseQueryBuilder](matchAllQuery) {
    _.foldLeft(boolQuery) {
      case (query, token) ⇒ query must {
        multiMatchQuery(token, f.memberEntity)
      }
    }
  }
}

private[text] object Query {

  def search(q: TextQuery) = new Query(q).search

  def count(q: TextQuery) = new Query(q).count
}
