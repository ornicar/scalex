package org.scalex
package util

import play.api.libs.json._
import scalaz.Monoid

private[scalex] object ScalexJson extends ScalexJson

private[scalex] trait ScalexJson {

  implicit final class OWritesDropDefaults[E](writes: OWrites[E]) {

    def dropDefaults: OWrites[E] = OWrites { (v: E) ⇒
      (writes writes v) |> { obj ⇒
        DropDefaults(obj).asOpt[JsObject] | obj
      }
    }
  }

  implicit final class OFormatDropDefaults[E](format: OFormat[E]) {

    def dropDefaults: OFormat[E] = OFormat(
      format,
      OWrites(format.writes).dropDefaults
    )
  }

  object DropDefaults {

    def apply(obj: JsObject): JsObject = apply(obj: JsValue).asOpt[JsObject] | obj

    def apply(js: JsValue): JsValue = js match {
      case JsObject(fields) ⇒ JsObject(fields map {
        case (key, value) ⇒ key -> apply(value)
      } filterNot {
        case (_, value) ⇒ droppable(value)
      })
      case JsArray(fields) ⇒ JsArray(fields map apply filterNot droppable)
      case _               ⇒ js
    }

    private def droppable(v: JsValue): Boolean = v match {
      case JsNull       ⇒ true
      case JsString("") ⇒ true
      case JsObject(fields) ⇒ fields forall {
        case (_, value) ⇒ droppable(value)
      }
      case JsArray(fields) ⇒ fields forall droppable
      case _               ⇒ false
    }
  }

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

    def dropDefaults: JsObject = DropDefaults(js)

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
