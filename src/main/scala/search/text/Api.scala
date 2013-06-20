package org.scalex
package search
package text

import util.Timer._

final class Api(engine: Engine[document.Doc]) {

  def search(tokens: List[Token]) = new {

    def in(scope: query.Scope): Results = {

      import Filters._

      println("Searching for %s in %s".format(tokens mkString " ", scope))

      val (res, time) = monitor {
        sort(
          boost(
            results(
              engine(scope, tokens)
            )))
      }

      println("Found %d results in %d ms".format(res.size, time))
      res
    }
  }
}
