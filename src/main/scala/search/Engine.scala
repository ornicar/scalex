package org.scalex
package search

import scala.concurrent.Future

import query.Scope

trait Engine {

  def search(tokens: List[Token], scope: Scope): Future[List[result.Result]]
}
