package ophir.dump

import scala.tools.nsc.doc.Universe
import scala.collection.mutable
import scala.tools.nsc.doc.model.{ TypeEntity => NscTypeEntity, _ }
import ophir.dump.model._

class Extractor {

  def makeFunctions(universe: Universe): List[ophir.model.Def] = {

    val functions = mutable.ListBuffer[ophir.model.Def]()
    val done = mutable.HashSet.empty[DocTemplateEntity]

    def gather(tpl: DocTemplateEntity): Unit = {
      if (!(done contains tpl)) {
        done += tpl

        val members = (tpl.methods ++ tpl.values) filterNot (_.isAbstract)

        //println("%s => %d functions" format (tpl, members.size))

        members map { m => functions += makeDef(m) }

        tpl.templates map gather
      }
    }

    gather(universe.rootPackage)

    functions.toList
  }

  private[this] def isValid(fun: Def) =
    fun.visibility.isPublic &&
    !fun.isImplicit &&
    !fun.isAbstract &&
    (
      fun.inTemplate.isInstanceOf[Trait]
      || fun.inTemplate.isInstanceOf[Object]
    )

  private[this] def makeDef(fun: NonTemplateMemberEntity) = fun match {
    case fun: Def => ophir.model.Def(
        fun.name
      , makeQualifiedName(fun.qualifiedName)
      , makeParent(fun.inTemplate)
      , makeTypeEntity(fun.resultType)
      , HtmlWriter.commentToHtml(fun.comment).toString
      , makeValueParams(fun.valueParams)
      , makeTypeParams(fun.typeParams)
      , makeTokens(makeQualifiedName(fun.qualifiedName))
    )
    case fun: Val => ophir.model.Def(
        fun.name
      , makeQualifiedName(fun.qualifiedName)
      , makeParent(fun.inTemplate)
      , makeTypeEntity(fun.resultType)
      , HtmlWriter.commentToHtml(fun.comment).toString
      , makeValueParams(Nil)
      , makeTypeParams(Nil)
      , makeTokens(makeQualifiedName(fun.qualifiedName))
    )
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

  private[this] def makeTypeEntity(t: NscTypeEntity): ophir.model.TypeEntityInterface = {
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
}
