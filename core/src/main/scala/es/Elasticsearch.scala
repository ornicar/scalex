package scalex
package es

import org.elasticsearch.node.NodeBuilder.nodeBuilder
import org.elasticsearch.client.Client

import model.Def

object Elasticsearch {

  def Connect(op: Client => Any) = {
    val node = nodeBuilder.node
    try {
      op(node.client)
    } finally {
      node.close()
    }
  }
}
