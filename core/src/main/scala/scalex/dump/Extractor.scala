package scalex.dump

import scala.tools.nsc.doc.Universe
import scala.collection.mutable
import scala.tools.nsc.doc.model.{ TypeEntity => NscTypeEntity, _ }
import scalex.dump.model._

class Extractor(logger: String => Unit, config: Dumper.Config) {

  def passFunctions(universe: Universe, callback: List[scalex.model.Def] => Any) {

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
    val qualifiedName = makeQualifiedName(fun.qualifiedName)
    val parent = makeParent(fun.inTemplate)
    val resultType = makeTypeEntity(fun.resultType)
    val valueParams = fun match {
      case fun: Def => makeValueParams(fun.valueParams)
      case fun: Val => makeValueParams(Nil)
    }
    val flatValueParams = valueParams.foldLeft(List[scalex.model.ValueParam]())((a, b) => a ::: b.params)
    val typeSig = scalex.model.RawTypeSig(
      parent.toTypeEntity :: (flatValueParams filter (!_.isImplicit) map (_.resultType)) ::: List(resultType))
    val normSig = typeSig.normalize.toString
    val sigTokens = makeSigTokens(List(normSig), config.aliases.toList) map (_.toLowerCase)

    fun match {
      case fun: Def => scalex.model.Def(
          fun.name
        , qualifiedName
        , parent
        , resultType
        , commentHtml
        , commentText
        , valueParams
        , makeTypeParams(fun.typeParams)
        , makeTokens(qualifiedName)
        , sigTokens
      )
      case fun: Val => scalex.model.Def(
          fun.name
        , qualifiedName
        , parent
        , resultType
        , commentHtml
        , commentText
        , valueParams
        , makeTypeParams(Nil)
        , makeTokens(qualifiedName)
        , sigTokens
      )
    }
  }

  private[this] def makeQualifiedName(name: String): String =
    name.replace("scala.", "")

  private[this] def makeTokens(name: String): List[String] =
    addAliases(scalex.model.Def.nameToTokens(name), config.aliases.toList)

  private[this] def addAliases(tokens: List[String], aliases: List[(String, String)]): List[String] = aliases match {
    case Nil => tokens
    case (a, b) :: rest => if (tokens contains a.toLowerCase) addAliases(b.toLowerCase :: tokens, rest) else addAliases(tokens, rest)
  }

  private[this] def makeSigTokens(sigs: List[String], aliases: List[(String, String)]): List[String] = aliases match {
    case Nil => sigs
    case (a, b) :: rest =>
      if (sigs.head contains a)
        addAliases(sigs.head.replace(a, b) :: sigs, rest)
      else
        addAliases(sigs, rest)
  }

  private[this] def makeTypeParams(tps: List[TypeParam]): List[scalex.model.TypeParam] = tps map { tp =>
    scalex.model.TypeParam(
        tp.name
      , makeQualifiedName(tp.qualifiedName)
      , tp.variance
      , tp.lo map makeTypeEntity
      , tp.hi map makeTypeEntity
      , makeTypeParams(tp.typeParams)
    )
  }

  private[this] def makeParent(p: DocTemplateEntity): scalex.model.Parent = p match {
    case o: Object =>
      scalex.model.Parent.makeObject(
          o.name
        , makeQualifiedName(o.qualifiedName)
      )
    case t: Trait =>
      scalex.model.Parent.makeTrait(
          t.name
        , makeQualifiedName(t.qualifiedName)
        , makeTypeParams(t.typeParams)
      )
  }

  private[this] def makeTypeEntity(t: NscTypeEntity): scalex.model.TypeEntity = {
    // convert to scalex TypeEntity, which is richer
    val te = t.asInstanceOf[TypeEntity]
    te.fullType
  }

  private[this] def makeValueParams(params: List[List[ValueParam]]) =
    params.map(vs => scalex.model.ValueParams(vs map makeValueParam))

  private[this] def makeValueParam(param: ValueParam): scalex.model.ValueParam =
      scalex.model.ValueParam(
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
