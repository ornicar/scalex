package ornicar.scalex
package search
package query

import scalaz.NonEmptyList

sealed trait Query

case class NameQuery(tokens: NonEmptyList[String]) extends Query {

  override def toString = tokens.list mkString " and "
}

case class ScopedQuery(query: Query, scope: QueryScope)
