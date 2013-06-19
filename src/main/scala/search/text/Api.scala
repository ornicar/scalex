package org.scalex
package search
package text

final class Api(engine: Engine[document.Doc]) {

  def search(tokens: List[Token]) = new {

    def in(scope: query.Scope): Results = {

      import Filters._

      sort(
        boost(
          results(
            engine(scope)(tokens)
          )))
    }
  }
}
