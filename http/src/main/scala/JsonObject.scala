package scalex
package http

import scalex.model.{Def, Block}

case class JsonObject(pairs: Map[String, Any]) {

  override def toString = pairs map renderPair mkString ("{", ", ", "}")

  def renderPair(pair: (String, Any)): String = pair match {
    case (k, v) => "\"%s\": %s" format (k, renderValue(v))
  }

  def renderValue(value: Any): String = value match {
    case v: JsonObject => v.toString
    case v: List[_] => v mkString ("[", ", ", "]")
    case v => quote(v.toString)
  }

  def removeNones: JsonObject = JsonObject(
    pairs map {
      case (k, Some(o: JsonObject)) => (k, o.removeNones)
      case (k, o: JsonObject) => (k, o.removeNones)
      case (k, Some(v)) => (k, v)
      case x => x
      } filter {
        case (k, None) => false
        case (k, Nil) => false
        case (k, o: JsonObject) if o.pairs.isEmpty => false
        case x => true
    }
  )

  /**
   * Quote a string according to "JSON rules".
   */
  def quote(s: String) = {
    val charCount = s.codePointCount(0, s.length)
    "\"" + 0.to(charCount - 1).map { idx =>
      s.codePointAt(s.offsetByCodePoints(0, idx)) match {
        case 0x0d => "\\r"
        case 0x0a => "\\n"
        case 0x09 => "\\t"
        case 0x22 => "\\\""
        case 0x5c => "\\\\"
        case 0x2f => "\\/" // to avoid sending "</"
        case c => quotedChar(c)
      }
    }.mkString("") + "\""
  }

  private[this] def quotedChar(codePoint: Int) = {
    codePoint match {
      case c if c > 0xffff =>
        val chars = Character.toChars(c)
        "\\u%04x\\u%04x".format(chars(0).toInt, chars(1).toInt)
      case c if c > 0x7e => "\\u%04x".format(c.toInt)
      case c => c.toChar
    }
  }
}

object JsonObject {

  def apply(pairs: (String, Any)*): JsonObject = JsonObject(pairs.toMap)

  def apply(fun: Def): JsonObject = {

    def optionBlock(b: Option[Block]): JsonObject =
      JsonObject("html" -> (b map (_.html)), "txt" -> (b map (_.txt)))

    def block(b: Block): JsonObject =
      JsonObject("html" -> b.html, "txt" -> b.txt)

    JsonObject(
      "name" -> fun.name,
      "qualifiedName" -> fun.qualifiedName,
      "parent" -> JsonObject(
        "name" -> fun.parent.name,
        "qualifiedName" -> fun.parent.qualifiedName,
        "typeParams" -> fun.parent.showTypeParams
      ),
      "comment" -> (fun.comment map { com =>
        JsonObject(
          "short" -> block(com.short),
          "body" -> block(com.body),
          "authors" -> (com.authors map block),
          "see" -> (com.see map block),
          "result" -> optionBlock(com.result),
          "throws" -> JsonObject(com.throws map { case (k, v) => (k.replace("_", ".") -> block(v)) }),
          "typeParams" -> JsonObject(com.typeParams map { case (k, v) => (k.replace("_", ".") -> block(v)) }),
          "valueParams" -> JsonObject(com.valueParams map { case (k, v) => (k.replace("_", ".") -> block(v)) }),
          "version" -> optionBlock(com.version),
          "since" -> optionBlock(com.since),
          "todo" -> (com.todo map block),
          "note" -> (com.note map block),
          "example" -> (com.example map block),
          "constructor" -> optionBlock(com.constructor),
          "source" -> com.source
        )
      }),
      "typeParams" -> fun.showTypeParams,
      "resultType" -> fun.resultType,
      "valueParams" -> fun.paramSignature,
      "signature" -> fun.signature,
      "package" -> fun.pack
    ) removeNones
  }
}
