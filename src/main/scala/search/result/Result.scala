package org.scalex
package search
package result

import document.Doc

case class Result(doc: Doc, score: Score) {

  override def toString = {
    List(doc.name, doc) :::
      (doc.member.comment ?? (_.summaryOrBody.txt)).toList
  } mkString "\n"
}
