package org.scalex
package util

import org.specs2.mutable._
import play.api.libs.json._
import ScalexJson._

class JsonSpec extends Specification {

  import Json.obj

  "drop defaults" should {
    "keep good stuff" in {
      val o = obj("foo" -> obj("a" -> 1, "b" -> 2))
      o.dropDefaults must_== o
    }
    "drop empty stuff" in {
      val o = obj(
        "foo" -> obj("a" -> JsArray(), "b" -> 2),
        "bar" -> "",
        "zob" -> (None: Option[Int]))
      o.dropDefaults must_== obj("foo" -> obj("b" -> 2))
    }
  }
}
