package org.scalex
package index

import java.io.Writer
import scala.collection._
import scala.tools.nsc.doc.base.{ comment ⇒ nscComment }
import scala.tools.nsc.doc.html._
import scala.tools.nsc.doc.{ model ⇒ nsc }
import scala.xml.dtd.{ DocType, PublicID }
import scala.xml.NodeSeq

private[index] object Html {

  def htmlToText(html: String): String =
    try {
      scala.xml.parsing.XhtmlParser(scala.io.Source.fromString("<span>" + html + "</span>")).text
    }
    catch {
      case e: scala.xml.parsing.FatalError ⇒ html
    }

  /**
   * Transforms an optional comment into an styled HTML tree representing its body if it is defined, or into an empty
   * node sequence if it is not.
   */
  // def commentToHtml(comment: Option[Comment]): NodeSeq =
  //   (comment map (commentToHtml(_))) getOrElse NodeSeq.Empty

  /** Transforms a comment into an styled HTML tree representing its body. */
  // def commentToHtml(comment: Comment): NodeSeq =
  //   bodyToHtml(comment.body)

  def bodyToHtml(body: nscComment.Body): NodeSeq =
    body.blocks flatMap (blockToHtml(_))

  def blockToHtml(block: nscComment.Block): NodeSeq = block match {
    case nscComment.Title(in, 1)  ⇒ <h3>{ inlineToHtml(in) }</h3>
    case nscComment.Title(in, 2)  ⇒ <h4>{ inlineToHtml(in) }</h4>
    case nscComment.Title(in, 3)  ⇒ <h5>{ inlineToHtml(in) }</h5>
    case nscComment.Title(in, _)  ⇒ <h6>{ inlineToHtml(in) }</h6>
    case nscComment.Paragraph(in) ⇒ <p>{ inlineToHtml(in) }</p>
    case nscComment.Code(data)    ⇒ <pre>{ scala.xml.Text(data) }</pre>
    case nscComment.UnorderedList(items) ⇒
      <ul>{ listItemsToHtml(items) }</ul>
    case nscComment.OrderedList(items, listStyle) ⇒
      <ol class={ listStyle }>{ listItemsToHtml(items) }</ol>
    case nscComment.DefinitionList(items) ⇒
      <dl>{ items map { case (t, d) ⇒ <dt>{ inlineToHtml(t) }</dt><dd>{ blockToHtml(d) }</dd> } }</dl>
    case nscComment.HorizontalRule() ⇒
      <hr/>
  }

  def listItemsToHtml(items: Seq[nscComment.Block]) =
    items.foldLeft(xml.NodeSeq.Empty) { (xmlList, item) ⇒
      item match {
        case nscComment.OrderedList(_, _) | nscComment.UnorderedList(_) ⇒ // html requires sub ULs to be put into the last LI
          xmlList.init ++ <li>{ xmlList.last.child ++ blockToHtml(item) }</li>
        case nscComment.Paragraph(inline) ⇒
          xmlList :+ <li>{ inlineToHtml(inline) }</li> // LIs are blocks, no need to use Ps
        case block ⇒
          xmlList :+ <li>{ blockToHtml(block) }</li>
      }
    }

  def inlineToHtml(inl: nscComment.Inline): NodeSeq = inl match {
    case nscComment.Chain(items)          ⇒ items flatMap (inlineToHtml(_))
    case nscComment.Italic(in)            ⇒ <i>{ inlineToHtml(in) }</i>
    case nscComment.Bold(in)              ⇒ <b>{ inlineToHtml(in) }</b>
    case nscComment.Underline(in)         ⇒ <u>{ inlineToHtml(in) }</u>
    case nscComment.Superscript(in)       ⇒ <sup>{ inlineToHtml(in) }</sup>
    case nscComment.Subscript(in)         ⇒ <sub>{ inlineToHtml(in) }</sub>
    case nscComment.Link(raw, title)      ⇒ <a href={ raw } target="_blank">{ inlineToHtml(title) }</a>
    case nscComment.Monospace(in)         ⇒ <code>{ inlineToHtml(in) }</code>
    case nscComment.Text(text)            ⇒ scala.xml.Text(text)
    case nscComment.Summary(in)           ⇒ inlineToHtml(in)
    case nscComment.HtmlTag(tag)          ⇒ scala.xml.Unparsed(tag)
    case nscComment.EntityLink(target, _) ⇒ inlineToHtml(target)
  }
}
