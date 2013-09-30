package org.scalex
package elastic

import akka.actor.ActorRef
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.source.Source
import com.sksamuel.elastic4s.{ ElasticDsl ⇒ ES }
import org.elasticsearch.action.ActionRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query._, FilterBuilders._, QueryBuilders._
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.{ search ⇒ esSearch }
import play.api.libs.json._

private[scalex] object api {

  case class Clear(typeName: Type, mapping: List[ES.FieldDefinition])

  case class Optimize(typeName: Type)

  case class IndexMany(typeName: Type, docs: List[(String, Source)])

  case class ThenDo(f: ActorRef ⇒ Fu[_])

  // sealed trait Request[A] {

  //   def in(indexName: String)(es: ElasticClient): A
  // }

  // case class Search(
  //     query: QueryDefinition,
  //     typeNames: Types,
  //     size: Int = 10,
  //     from: Int = 0,
  //     sortings: Iterable[SearchParameterTypes.Sorting] = Nil) extends Request[SearchResponse] {

  //   val explain = none[Boolean]

  //   def in(indexName: String)(es: ElasticClient): SearchResponse =
  //     es.search(Seq(indexName), typeNames, query,
  //       filter = none,
  //       sortings = sortings,
  //       from = from.some,
  //       size = size.some,
  //       explain = explain
  //     )
  // }

  // case class Count(
  //     query: QueryBuilder,
  //     typeNames: Types) extends Request[Int] {

  //   def in(indexName: String)(es: ElasticClient): Int = {
  //     es.search(Seq(indexName), typeNames, query,
  //       filter = none,
  //       searchType = SearchType.COUNT.some
  //     )
  //   }.getHits.totalHits.toInt
  // }
}
