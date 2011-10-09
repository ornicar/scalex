package ophir.dump

import scala.tools.nsc.doc.Universe
import scala.collection.mutable
import scala.tools.nsc.doc.model.{ TypeEntity => NscTypeEntity, _ }
import ophir.dump.model._

class Extractor(logger: String => Unit) {

  def passFunctions(universe: Universe, callback: List[ophir.model.Def] => Any) {

    val done = mutable.HashSet.empty[Int]

    def gather(tpl: DocTemplateEntity): Unit = {
      val tplHashCode = tpl.hashCode
      if (!(done contains tplHashCode)) {
        done += tplHashCode

        val members = (tpl.methods ++ tpl.values) filterNot (_.isAbstract)

        println("%s => %d functions" format (tpl, members.size))

        callback(members map makeDef)

        tpl.templates foreach gather
      }
    }

    gather(universe.rootPackage)
  }

  private[this] def makeDef(fun: NonTemplateMemberEntity) = {

    val commentHtml = TextUtil.removeTrailingNewline(HtmlWriter.commentToHtml(fun.comment).toString)
    val commentText =
      try { TextUtil.htmlToText(commentHtml) }
      catch { case e: scala.xml.parsing.FatalError => logger("--" + e.toString); "" }

    fun match {
      case fun: Def => ophir.model.Def(
          fun.name
        , makeQualifiedName(fun.qualifiedName)
        , makeParent(fun.inTemplate)
        , makeTypeEntity(fun.resultType)
        , commentHtml
        , commentText
        , makeValueParams(fun.valueParams)
        , makeTypeParams(fun.typeParams)
        , makeTokens(makeQualifiedName(fun.qualifiedName))
      )
      case fun: Val => ophir.model.Def(
          fun.name
        , makeQualifiedName(fun.qualifiedName)
        , makeParent(fun.inTemplate)
        , makeTypeEntity(fun.resultType)
        , commentHtml
        , commentText
        , makeValueParams(Nil)
        , makeTypeParams(Nil)
        , makeTokens(makeQualifiedName(fun.qualifiedName))
      )
    }
  }

  private[this] def makeQualifiedName(name: String): String =
    name.replace("scala.", "")

  private[this] def makeTokens(name: String): List[String] =
    ophir.model.Def.nameToTokens(name)

  private[this] def makeTypeParams(tps: List[TypeParam]): List[ophir.model.TypeParam] = tps map { tp =>
    ophir.model.TypeParam(
        tp.name
      , makeQualifiedName(tp.qualifiedName)
      , tp.variance
      , tp.lo map makeTypeEntity
      , tp.hi map makeTypeEntity
      , makeTypeParams(tp.typeParams)
    )
  }

  private[this] def makeParent(p: DocTemplateEntity): ophir.model.Parent = p match {
    case o: Object =>
      ophir.model.Parent.makeObject(
          o.name
        , makeQualifiedName(o.qualifiedName)
      )
    case t: Trait =>
      ophir.model.Parent.makeTrait(
          t.name
        , makeQualifiedName(t.qualifiedName)
        , makeTypeParams(t.typeParams)
      )
  }

  private[this] def makeTypeEntity(t: NscTypeEntity): ophir.model.TypeEntity = {
    // convert to ophir TypeEntity, which is richer
    val te = t.asInstanceOf[TypeEntity]
    te.fullType
  }

  private[this] def makeValueParams(params: List[List[ValueParam]]) =
    params.map(vs => ophir.model.ValueParams(vs map makeValueParam))

  private[this] def makeValueParam(param: ValueParam): ophir.model.ValueParam =
      ophir.model.ValueParam(
          param.name
        , makeTypeEntity(param.resultType)
        , param.defaultValue map (_.expression)
        , param.isImplicit
      )

  private object TextUtil {

    def htmlToText(html: String): String =
      scala.xml.parsing.XhtmlParser(
        scala.io.Source.fromString("<span>"+html.trim+"</span>")
      ).text.lines map (_.trim) mkString

    def removeTrailingNewline(text: String): String =
      text.replaceAll("""(^\n|\n$)""", "")
  }
}
