package org.scalex
package search
package text

import com.sksamuel.elastic4s
import com.sksamuel.elastic4s.{ ElasticDsl => ES }
import org.elasticsearch.index.query._, QueryBuilders._, FilterBuilders._

import Index.{ fields ⇒ F }
import model.Project

private[search] case class Query(
    raw: String,
    tokens: List[String],
    scope: query.Scope,
    pagination: query.Pagination) extends query.Query {

  def definition: elastic4s.QueryDefinition =
    tokens.toNel.fold[elastic4s.QueryDefinition](ES.all) {
      _.foldLeft(new elastic4s.BoolQueryDefinition) {
        case (q, token) ⇒ q must (ES.multiMatchQuery(token) fields F.memberEntity)
      }
    }

  override def toString = "\"%s\" in %s".format(tokens mkString " and ", scope)
}
