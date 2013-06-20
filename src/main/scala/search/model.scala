package org.scalex
package search

import document.Doc

case class TextQuery(tokens: List[Token], scope: query.Scope)

case class Result(doc: Doc, score: Score)
