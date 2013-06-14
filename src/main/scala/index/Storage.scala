package ornicar.scalex
package index

import play.api.libs.json._
import java.io._

import model._

object IndexRepo {

  // def toJson(db: Database): JsValue = Format.database writes db

  // def read(file: File): Database = { fromFile[Database](file) }

  private object Format {

    // def database = Json.format[Database]

    implicit def memberTemplate = Json.format[MemberTemplate]
    implicit def docTemplate = Json.format[DocTemplate]
    implicit def template = Json.format[Template]
    implicit def higherKinded = Json.format[HigherKinded]
    implicit def templateAndType = Json.format[TemplateAndType]
    implicit def entity = Json.format[Entity]
    implicit def valueParam = Json.format[ValueParam]
    implicit def member = Json.format[Member]
    implicit def implicitConversion = Json.format[ImplicitConversion]
  }
}
