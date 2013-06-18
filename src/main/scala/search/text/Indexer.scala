package ornicar.scalex
package search
package text

import document.Doc

private[search] object Indexer {

  def apply(docs: List[Doc]): Index[Doc] = {

    import scala.collection.mutable

    val hash = mutable.Map[String, mutable.ListBuffer[Doc]]()

    for {
      doc ← docs
      key ← doc.tokenize
    } hash.getOrElseUpdate(key, mutable.ListBuffer()) += doc

    new Index(hash mapValues (_.toList) toMap)
  }
}
