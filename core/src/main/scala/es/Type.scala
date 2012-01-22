package scalex
package es

import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.flush.FlushRequest
import org.elasticsearch.indices.IndexMissingException

class Type(indexName: String, name: String, client: Client) {

  val bulkSize = 1000
  def log(a: Any) {
    println("ELASTICSEARCH : " + a)
  }

  def populate[A](objs: Iterable[A], map: A => XContentBuilder, id: A => String) = {
    val total = objs.size
    val groups = objs grouped bulkSize
    groups.zipWithIndex foreach {
      case (xs, i) => {
        log("EXECUTE BULK (%d/%d)".format(xs.size + (i * bulkSize), total))
        val bulk = client.prepareBulk
        xs foreach { obj =>
          bulk add store(map(obj), id(obj))
        }
        bulk.execute.actionGet
      }
    }
    flush()
  }

  def store(obj: XContentBuilder, id: String) = {
    client.prepareIndex(indexName, name, id) setSource obj
  }

  def drop() {
    log("DROP")
    try {
      client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet
      flush()
    } catch {
      case e: IndexMissingException =>
    }
  }

  private def flush() {
    log("FLUSH")
    client.admin().indices().flush(new FlushRequest(indexName).refresh(true)).actionGet
  }
}
