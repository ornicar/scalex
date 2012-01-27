package scalex
package test.search

import search._
import model._

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

trait WithSearch {

  type ValidSeq[A] = Validation[String, Seq[A]]

  def query(q: String) = RawQuery(q, 1, 10)

  def analyze(q: String) = query(q).analyze

  def search(q: String): Validation[String, Results] = search(query(q))

  def search(q: RawQuery): Validation[String, Results] = Search find q

  def search(q: String, nb: Int): ValidSeq[Def] =
    search(RawQuery(q, 1, nb)).map(_.defs)

  def searchNames(q: String, nb: Int): ValidSeq[String] =
    search(q, nb).map(ds => ds.map(_.qualifiedName))

  def searchNames(q: String): ValidSeq[String] = searchNames(q, 10)
}
