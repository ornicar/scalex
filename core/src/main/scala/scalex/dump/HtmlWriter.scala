package scalex.dump

import scala.tools.nsc.doc.model._
import scala.tools.nsc.doc.model.comment._
import scala.xml.NodeSeq

object HtmlWriter {
  /** Transforms an optional comment into an styled HTML tree representing its body if it is defined, or into an empty
    * node sequence if it is not. */
  def commentToHtml(comment: Option[Comment]): String =
    nl2br(((comment map (commentToHtml(_))) getOrElse NodeSeq.Empty).toString)

  /** Transforms a comment into an styled HTML tree representing its body. */
  def commentToHtml(comment: Comment): NodeSeq =
    bodyToHtml(comment.body)

  private def bodyToHtml(body: Body): NodeSeq =
    body.blocks flatMap (blockToHtml(_))

  private def nl2br(html: String): String =
    html.replace("\n", "<br />")

  private def blockToHtml(block: Block): NodeSeq = block match {
    case Title(in, 1) => <h3>{ inlineToHtml(in) }</h3>
    case Title(in, 2) => <h4>{ inlineToHtml(in) }</h4>
    case Title(in, 3) => <h5>{ inlineToHtml(in) }</h5>
    case Title(in, _) => <h6>{ inlineToHtml(in) }</h6>
    case Paragraph(in) => <p>{ inlineToHtml(in) }</p>
    case Code(data) => <pre>{ xml.Text(data) }</pre>
    case UnorderedList(items) =>
      <ul>{ listItemsToHtml(items) }</ul>
    case OrderedList(items, listStyle) =>
      <ol class={ listStyle }>{ listItemsToHtml(items) }</ol>
    case DefinitionList(items) =>
      val definitions = items map { case (t, d) => <dt>{ inlineToHtml(t) }</dt><dd>{ blockToHtml(d) }</dd> }
      <dl>{ definitions }</dl>
    case HorizontalRule() =>
      <hr/>
  }

  def listItemsToHtml(items: Seq[Block]) =
    items.foldLeft(xml.NodeSeq.Empty){ (xmlList, item) =>
      item match {
        case OrderedList(_, _) | UnorderedList(_) =>  // html requires sub ULs to be put into the last LI
          xmlList.init ++ <li>{ xmlList.last.child ++ blockToHtml(item) }</li>
        case Paragraph(inline) =>
          xmlList :+ <li>{ inlineToHtml(inline) }</li>  // LIs are blocks, no need to use Ps
        case block =>
          xmlList :+ <li>{ blockToHtml(block) }</li>
      }
  }

  def inlineToHtml(inl: Inline): NodeSeq = inl match {
    case Chain(items) => items flatMap (inlineToHtml(_))
    case Italic(in) => <i>{ inlineToHtml(in) }</i>
    case Bold(in) => <b>{ inlineToHtml(in) }</b>
    case Underline(in) => <u>{ inlineToHtml(in) }</u>
    case Superscript(in) => <sup>{ inlineToHtml(in) }</sup>
    case Subscript(in) => <sub>{ inlineToHtml(in) }</sub>
    case Link(raw, title) => <a href={ raw }>{ inlineToHtml(title) }</a>
    case EntityLink(entity) => templateToHtml(entity)
    case Monospace(text) => <code>{ xml.Text(text) }</code>
    case Text(text) => xml.Text(text)
    case Summary(in) => inlineToHtml(in)
    case HtmlTag(tag) => xml.Unparsed(tag)
  }

  def typeToHtml(tpe: TypeEntity, hasLinks: Boolean): NodeSeq = {
    val string = tpe.name
    def toLinksOut(inPos: Int, starts: List[Int]): NodeSeq = {
      if (starts.isEmpty && (inPos == string.length))
        NodeSeq.Empty
      else if (starts.isEmpty)
        xml.Text(string.slice(inPos, string.length))
      else if (inPos == starts.head)
        toLinksIn(inPos, starts)
      else {
        xml.Text(string.slice(inPos, starts.head)) ++ toLinksIn(starts.head, starts)
      }
    }
    def toLinksIn(inPos: Int, starts: List[Int]): NodeSeq = {
      val (tpl, width) = tpe.refEntity(inPos)
      (tpl match {
        case dtpl:DocTemplateEntity if hasLinks =>
          <span class="extype" name={ dtpl.qualifiedName }>{
            string.slice(inPos, inPos + width)
          }</span>
        case tpl =>
          <span class="extype" name={ tpl.qualifiedName }>{ string.slice(inPos, inPos + width) }</span>
      }) ++ toLinksOut(inPos + width, starts.tail)
    }
    if (hasLinks)
      toLinksOut(0, tpe.refEntity.keySet.toList)
    else
      xml.Text(string)
  }

  /** Returns the HTML code that represents the template in `tpl` as a hyperlinked name. */
  def templateToHtml(tpl: TemplateEntity) = tpl match {
    case dTpl: DocTemplateEntity =>
      xml.Text(dTpl.name)
    case ndTpl: NoDocTemplate =>
      xml.Text(ndTpl.name)
  }
}
