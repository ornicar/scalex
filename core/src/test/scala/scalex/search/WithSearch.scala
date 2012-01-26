package scalex
package test.search

import search._
import model._
import com.github.ornicar.paginator._

trait WithSearch {

  type EitherSeq[A] = Either[String, Seq[A]]

  def search(query: String): Either[String, Results] = search(Query(query, 1, 10))

  def search(query: Query): Either[String, Results] = Search find query

  def search(query: String, nb: Int): EitherSeq[Def] =
    search(Query(query, 1, nb)).right.map(_.defs)

  def searchNames(query: String, nb: Int): EitherSeq[String] =
    search(query, nb).right.map(ds => ds.map(_.qualifiedName))

  def searchNames(query: String): EitherSeq[String] = searchNames(query, 10)
}
