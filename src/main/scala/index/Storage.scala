package ornicar.scalex
package index

import play.api.libs.json._
import java.io._

import model.Database

object IndexRepo {

  def toJson(db: Database): JsValue = Format.database writes db

  // def read(file: File): Database = { fromFile[Database](file) }

  private object Format {

    def database: Format[Database] = Json.format[Database]
  }
}
