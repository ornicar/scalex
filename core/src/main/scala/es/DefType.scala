package scalex
package es

object DefType {

  val indexName = "scalex"
  val typeName = "def"

  def apply[A](op: Type => A) = Elasticsearch Connect { client =>
    op(new Type(indexName, typeName, client))
  }
}
