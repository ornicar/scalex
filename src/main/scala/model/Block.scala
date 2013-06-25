package org.scalex
package model

case class Block(
  txt: String,
  // let empty if similar to txt
  html: Option[String])

object Block extends Function2[String, Option[String], Block] {

  def apply(txt: String, html: String): Block = {
    val t = txt.trim
    val h = trimHtml(html)
    val ho = (h != t) option h filter (_.nonEmpty)
    Block(t, ho)
  }

  private val trimHtmlRegex =
    """^$void<p>$void(.+)$void</p>$void$""".replace("$void", """[\n\s]*""").r

  def trimHtml(str: String): String = str.trim match {
    case trimHtmlRegex(content) ⇒ content
    case content                ⇒ content
  }

}
