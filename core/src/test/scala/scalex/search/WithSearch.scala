package scalex
package search

import model._

import com.github.ornicar.paginator._
import scalaz.Validation
import scalaz.Scalaz.{ success, failure }
import org.specs2.matcher.MatchResult
import org.specs2.matcher.Matcher

trait WithSearch extends ScalexTest {

  lazy val env = new Env

  type ValidSeq[A] = Validation[String, Seq[A]]

  def query(q: String) = RawQuery(q, 1, 10)

  def analyze(q: String) = query(q).analyze

  def search(q: String): Validation[String, Results] = search(query(q))

  def search(q: RawQuery): Validation[String, Results] = env.engine find q

  def search(q: String, nb: Int): ValidSeq[Def] =
    search(RawQuery(q, 1, nb)).map(_.defs)

  def searchNames(q: String, nb: Int): ValidSeq[String] =
    search(q, nb).map(ds ⇒ ds.map(_.qualifiedName))

  def searchNames(q: String): ValidSeq[String] = searchNames(q, 10)

  class MatchableSearch(search: String) {

    def finds(name: String): MatchResult[ValidSeq[String]] =
      searchNames(search, 300) must findName(name)

    def finds(names: Seq[String]): MatchResult[ValidSeq[String]] =
      searchNames(search, 300) must findNames(names)

    def findsNothing: MatchResult[ValidSeq[String]] =
      searchNames(search) must beSuccess.like {
        case names ⇒ names must beEmpty
      }

    private def findName(name: String): Matcher[ValidSeq[String]] = beSuccess.like {
      case elems ⇒ elems must contain(name)
    }

    private def findNames(names: Seq[String]): Matcher[ValidSeq[String]] = beSuccess.like {
      case elems ⇒ elems must containAllOf(names).inOrder
    }
  }
}
