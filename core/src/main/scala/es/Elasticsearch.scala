package scalex
package es

import org.elasticsearch.node.NodeBuilder.nodeBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentFactory._
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.flush.FlushRequest

import model.Def

object Elasticsearch {

  val indexName = "scalex"
  val typeName = "def"

  def Connect (op: Client => Any) = {
    val node = makeNode
    try {
      op(node.client)
    } finally {
      node.close()
    }
  }

  def populate(defs: Iterator[Def]) = Connect { client =>
    drop(client)
    val bulk = client.prepareBulk
    val s = store(client)_
    defs foreach { d => bulk add s(d) }
    bulk.execute.actionGet
    flush(client)
  }

  def store(client: Client)(d: Def) =
    client.prepareIndex(indexName, typeName, d.id)
      .setSource(Mapper defToJson d)

  def drop(client: Client) =
    client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet

  def flush(client: Client) =
    client.admin().indices().flush(new FlushRequest(indexName).refresh(true)).actionGet

  private def makeNode = nodeBuilder.node
}
