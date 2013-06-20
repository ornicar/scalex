package elastic

import org.elasticsearch.action.ActionRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query._, FilterBuilders._, QueryBuilders._
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.search._, facet._, terms._, sort._, SortBuilders._, builder._
import scalastic.elasticsearch.SearchParameterTypes
import scalastic.elasticsearch.Indexer

object Request {

  case class Search(
      query: QueryBuilder,
      filter: Option[FilterBuilder] = None,
      size: Int = 10,
      from: Int = 0,
      sortings: Iterable[SearchParameterTypes.Sorting] = Nil) {

    val explain = none[Boolean]

    def in(indexName: String, typeName: String)(es: Indexer): SearchResponse =
      es.search(Seq(indexName), Seq(typeName), query,
        filter = filter,
        sortings = sortings,
        from = from.some,
        size = size.some,
        explain = explain
      )
  }

  case class Count(
      query: QueryBuilder,
      filter: Option[FilterBuilder] = None) {

    def in(indexName: String, typeName: String)(es: Indexer): Int = {
      es.search(Seq(indexName), Seq(typeName), query,
        filter = filter,
        searchType = SearchType.COUNT.some
      )
    }.getHits.totalHits.toInt
  }

  def decomposeTextQuery(text: String): List[String] =
    text.trim.toLowerCase.replace("+", " ").split(" ").toList
}
