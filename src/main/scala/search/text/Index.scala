package org.scalex
package search
package text

import scala.collection.immutable._

private[text] final class Index[A](hash: Map[Token, List[A]]) {

  lazy val keys = hash.keySet

  def apply(token: Token): List[A] = ~(hash get token)

  def describe = "%s\nBest tokens:\n%s".format(
    "%d tokens, %d documents, %d unique documents".format(
      keys.size,
      countDocuments,
      countUniqueDocuments),
    bestTokens take 5 map {
      case (token, nb) ⇒ "- %s -> %d".format(token, nb)
    } mkString "\n"
  )

  def bestTokens: List[(Token, Int)] = hash.toList.sortBy(-_._2.size) map {
    case (token, docs) ⇒ token -> docs.size
  }

  def countDocuments = hash.map(_._2.size).sum

  def countUniqueDocuments = hash.map(_._2).flatten.toList.distinct.size

  override def toString = hash map {
    case (token, docs) ⇒ token + docs.map(_.toString).mkString(", ")
  } mkString "\n"
}
