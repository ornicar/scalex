package scalex
package test.search

import search._
import model._

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

trait WithSearch {

  type ValidSeq[A] = Validation[String, Seq[A]]

  def search(query: String): Validation[String, Results] = search(RawQuery(query, 1, 10))

  def search(query: RawQuery): Validation[String, Results] = Search find query

  def search(query: String, nb: Int): ValidSeq[Def] =
    search(RawQuery(query, 1, nb)).map(_.defs)

  def searchNames(query: String, nb: Int): ValidSeq[String] =
    search(query, nb).map(ds => ds.map(_.qualifiedName))

  def searchNames(query: String): ValidSeq[String] = searchNames(query, 10)
}
