package org.scalex
package search
package text

import document.{ Doc, Template }

private[text] object Filters {

  def sort(results: Results): Results = results sortBy {
    case Result(doc, score) ⇒ (-score, doc.qualifiedName, doc.declaration.size)
  }

  def boost(results: Results): Results = results map {
    case Result(doc: Template, score) ⇒ Result(doc, (score + 1))
    case result                       ⇒ result
  }

  def results(fragment: Fragment[Doc]): Results = fragment.toList map {
    case (doc, score) ⇒ Result(doc, score)
  }
}
