package scalex

import org.specs2.mutable._
import org.specs2.matcher.MatchResult
import org.specs2.matcher.Matcher
import ornicar.scalalib.test._

trait ScalexTest
    extends Specification
    with OrnicarValidationMatchers
    with ScalazIOMatchers
    with Fixtures {

  protected def like(expr: String): PartialFunction[String, MatchResult[String]] = {
    case a => a must =~(expr)
  }
}

