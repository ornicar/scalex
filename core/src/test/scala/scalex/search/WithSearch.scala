package scalex.test
package search

import scalex.Env
import scalex.search._
import scalex.model._

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }

trait WithSearch {

  def env = new Env

  type ValidSeq[A] = Validation[String, Seq[A]]

  def query(q: String) = RawQuery(q, 1, 10)

  def analyze(q: String) = query(q).analyze

  def search(q: String): Validation[String, Results] = search(query(q))

  def search(q: RawQuery): Validation[String, Results] = env.engine find q

  def search(q: String, nb: Int): ValidSeq[Def] =
    search(RawQuery(q, 1, nb)).map(_.defs)

  def searchNames(q: String, nb: Int): ValidSeq[String] =
    search(q, nb).map(ds => ds.map(_.qualifiedName))

  def searchNames(q: String): ValidSeq[String] = searchNames(q, 10)
}
