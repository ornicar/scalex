package org.scalex
package search
package result

import document.Doc

case class Result(doc: Doc, score: Score) {

  override def toString =
    """%s
%s
%s""".format(doc.name, doc, doc.member.comment.pp ?? (_.summaryOrBody.txt))
}
