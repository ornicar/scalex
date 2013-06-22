package org.scalex
package search
package query

import scalaz.NonEmptyList

private[search] case class Pagination(currentPage: Int, maxPerPage: Int)

private[search] sealed trait Query {
  def scope: Scope
  def pagination: Pagination
}

private[search] case class TextQuery(
    tokens: List[String],
    scope: Scope,
    pagination: Pagination) extends Query {

  override def toString = "\"%s\" in %s".format(tokens mkString " and ", scope)
}

private[search] case class SigQuery(
    sig: String,
    scope: Scope,
    pagination: Pagination) extends Query {
}
