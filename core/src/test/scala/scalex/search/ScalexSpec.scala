package scalex.test

import org.specs2.mutable._
import org.specs2.matcher.MatchResult
import org.specs2.matcher.Matcher

trait ScalexSpec extends Specification with ScalazMatchers {

  protected def like(expr: String): PartialFunction[String, MatchResult[String]] = {
    case a => a must =~(expr)
  }
}
