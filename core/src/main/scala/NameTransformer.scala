package scalex

// from scala reflect src/library/scala/reflect/NameTransformer.scala
object NameTransformer {

  def apply(name: String): String =
    replacements.foldLeft(name) {
      case (n, (k, v)) ⇒ n.replace(k, v)
    }

  val replacements = List(
    "~" -> "$tilde",
    "=" -> "$eq",
    "<" -> "$less",
    ">" -> "$greater",
    "!" -> "$bang",
    "#" -> "$hash",
    "%" -> "$percent",
    "^" -> "$up",
    "&" -> "$amp",
    "|" -> "$bar",
    "*" -> "$times",
    "/" -> "$div",
    "+" -> "$plus",
    "-" -> "$minus",
    ":" -> "$colon",
    "\\" -> "$bslash",
    "?" -> "$qmark",
    "@" -> "$at")
}
