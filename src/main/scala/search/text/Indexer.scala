package org.scalex
package search
package text

import document.{ Doc, Docs, ScopedDocs }

private[search] object Indexer {

  def scoped(scopedDocs: ScopedDocs) = new ScopedIndex[Doc](
    indices = scopedDocs mapValues apply
  )

  def apply(docs: Docs): Index[Doc] = {

    import scala.collection.mutable

    val hash = mutable.Map[String, mutable.ListBuffer[Doc]]()

    for {
      doc ← docs
      key ← doc.tokenize
    } hash.getOrElseUpdate(key, mutable.ListBuffer()) += doc

    new Index(hash mapValues (_.toList) toMap)
  }
}
