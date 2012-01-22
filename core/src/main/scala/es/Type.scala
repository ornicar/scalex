package scalex
package es

import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.flush.FlushRequest
import org.elasticsearch.indices.IndexMissingException

class Type(name: String, indexName: String, client: Client) {

  def populate[A](objs: Iterator[A], map: A => XContentBuilder, id: A => String) = {
    drop
    val bulk = client.prepareBulk
    objs foreach { obj => bulk add store(map(obj), id(obj)) }
    bulk.execute.actionGet
    flush
  }

  def store(obj: XContentBuilder, id: String) =
    client.prepareIndex(indexName, name, id) setSource obj

  def drop = try {
    client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet
  } catch {
    case e: IndexMissingException =>
  }

  def flush =
    client.admin().indices().flush(new FlushRequest(indexName).refresh(true)).actionGet
}
