package org.scalex
package search
package text

import com.sksamuel.elastic4s
import com.sksamuel.elastic4s.{ ElasticDsl ⇒ ES }
import org.elasticsearch.index.query._, QueryBuilders._, FilterBuilders._

import Index.{ fields ⇒ F }
import model.Project

private[search] case class Query(
    raw: String,
    tokens: List[String],
    scope: query.Scope,
    pagination: query.Pagination) extends query.Query {

  def definition = Query definition tokens

  override def toString = "\"%s\" in %s".format(tokens mkString " and ", scope)
}

private[search] case class Count(
    tokens: List[String],
    scope: query.Scope) extends query.Count {

  def definition = Query definition tokens
}

private[search] object Query {

  def definition(tokens: List[String]): elastic4s.QueryDefinition =
    tokens.toNel.fold[elastic4s.QueryDefinition](ES.all) {
      _.foldLeft(new elastic4s.BoolQueryDefinition) {
        case (q, token) ⇒ q must (ES.multiMatchQuery(token) fields F.memberEntity)
      }
    }
}
