package org.scalex
package search
package query

import com.sksamuel.elastic4s
import com.sksamuel.elastic4s.{ ElasticDsl ⇒ ES }

private[search] case class Pagination(currentPage: Int, maxPerPage: Int)

private[search] trait Query {
  def raw: String
  def scope: Scope
  def pagination: Pagination
  def definition: elastic4s.QueryDefinition
}
