package org.scalex
package search

import query.Scope

trait Engine {

  def search(tokens: List[Token], scope: Scope): Fu[List[result.Result]]
}
