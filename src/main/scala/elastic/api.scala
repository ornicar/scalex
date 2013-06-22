package org.scalex
package elastic

import scala.concurrent.Future

import akka.actor.ActorRef
import org.elasticsearch.action.ActionRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query._, FilterBuilders._, QueryBuilders._
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.search._, facet._, terms._, sort._, SortBuilders._, builder._
import play.api.libs.json._
import scalastic.elasticsearch.Indexer
import scalastic.elasticsearch.SearchParameterTypes

private[scalex] object api {

  case class Clear(typeName: Type, mapping: JsObject)

  case class Optimize(typeName: Type)

  case class IndexMany(typeName: Type, docs: List[(String, JsObject)])

  case class ThenDo(f: ActorRef ⇒ Future[_])

  case object AwaitReady

  sealed trait Request[A] {

    def in(indexName: String)(indexer: Indexer): A
  }

  case class Search(
      query: QueryBuilder,
      typeNames: Types,
      size: Int = 10,
      from: Int = 0,
      sortings: Iterable[SearchParameterTypes.Sorting] = Nil) extends Request[SearchResponse] {

    val explain = none[Boolean]

    def in(indexName: String)(es: Indexer): SearchResponse =
      es.search(Seq(indexName), typeNames, query,
        filter = none,
        sortings = sortings,
        from = from.some,
        size = size.some,
        explain = explain
      )
  }

  case class Count(
      query: QueryBuilder,
      typeNames: Types) extends Request[Int] {

    def in(indexName: String)(es: Indexer): Int = {
      es.search(Seq(indexName), typeNames, query,
        filter = none,
        searchType = SearchType.COUNT.some
      )
    }.getHits.totalHits.toInt
  }
}
