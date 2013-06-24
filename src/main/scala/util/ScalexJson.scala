package org.scalex
package util

import play.api.libs.json._
import scalaz.Monoid

private[scalex] object ScalexJson extends ScalexJson

private[scalex] trait ScalexJson {

  implicit final class OFormatMonoid[E: Monoid](format: OFormat[E]) {

    def default: OFormat[E] = OFormat(
      Reads { v ⇒
        format reads v match {
          case s: JsSuccess[_] ⇒ s: JsResult[E]
          case _               ⇒ JsSuccess(∅[E])
        }
      },
      format)
  }

  implicit final class ScalexJsObject(js: JsObject) {

    def str(key: String): Option[String] =
      (js \ key).asOpt[String]

    def int(key: String): Option[Int] =
      (js \ key).asOpt[Int]

    def long(key: String): Option[Long] =
      (js \ key).asOpt[Long]

    def boolean(key: String): Option[Boolean] =
      (js \ key).asOpt[Boolean]

    def obj(key: String): Option[JsObject] =
      (js \ key).asOpt[JsObject]

    def arr(key: String): Option[JsArray] =
      (js \ key).asOpt[JsArray]

    def get[A: Reads](key: String): Option[A] =
      (js \ key).asOpt[A]
  }

  implicit final class ScalexJsValue(js: JsValue) {

    def str(key: String): Option[String] =
      js.asOpt[JsObject] flatMap { obj ⇒
        (obj \ key).asOpt[String]
      }

    def int(key: String): Option[Int] =
      js.asOpt[JsObject] flatMap { obj ⇒
        (obj \ key).asOpt[Int]
      }

    def long(key: String): Option[Long] =
      js.asOpt[JsObject] flatMap { obj ⇒
        (obj \ key).asOpt[Long]
      }

    def obj(key: String): Option[JsObject] =
      js.asOpt[JsObject] flatMap { obj ⇒
        (obj \ key).asOpt[JsObject]
      }

    def arr(key: String): Option[JsArray] =
      js.asOpt[JsObject] flatMap { obj ⇒
        (obj \ key).asOpt[JsArray]
      }
  }
}
