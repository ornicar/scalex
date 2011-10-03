package ophir.dump

import ophir.model
import scala.tools.nsc.doc.Universe
import scala.collection.mutable
import scala.tools.nsc.doc.model._

class ModelFactory {

  def makeModel(universe: Universe): List[model.Def] = {

    val functions = mutable.ListBuffer[model.Def]()
    val done = mutable.HashSet.empty[DocTemplateEntity]

    def gather(tpl: DocTemplateEntity): Unit = {
      if (!(done contains tpl)) {
        done += tpl

        val members = (tpl.methods ++ tpl.values) filterNot (_.isAbstract)

        println("%s => %d functions" format (tpl, members.size))

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
    case fun: Def => model.Def(
        fun.name
      , makeQualifiedName(fun.qualifiedName)
      , makeParent(fun.inTemplate)
      , makeTypeEntity(fun.resultType)
      , HtmlWriter.commentToHtml(fun.comment).toString
      , makeValueParams(fun.valueParams)
      , makeTypeParams(fun.typeParams)
      , makeTokens(makeQualifiedName(fun.qualifiedName))
    )
    case fun: Val => model.Def(
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
    model.Def.nameToTokens(name)

  private[this] def makeTypeParams(tps: List[TypeParam]): List[model.TypeParam] = tps map { tp =>
    model.TypeParam(
        tp.name
      , makeQualifiedName(tp.qualifiedName)
      , tp.variance
      , tp.lo map makeTypeEntity
      , tp.hi map makeTypeEntity
      , makeTypeParams(tp.typeParams)
    )
  }

  private[this] def makeParent(p: DocTemplateEntity): model.Parent = p match {
    case o: Object =>
      model.Parent.makeObject(
          o.name
        , makeQualifiedName(o.qualifiedName)
      )
    case t: Trait =>
      model.Parent.makeTrait(
          t.name
        , makeQualifiedName(t.qualifiedName)
        , makeTypeParams(t.typeParams)
      )
  }

  private[this] def makeTypeEntity(t: TypeEntity) = {
    // remove parenthesis around single types
    // (A) -> A
    val regex = """\((\w+)\)""".r
    val name = regex.replaceAllIn(t.name, m => m.group(1))
    model.TypeEntity(name)
  }

  private[this] def makeValueParams(params: List[List[ValueParam]]) =
    params.map(vs =>
      model.ValueParams(vs.map(v =>
        model.ValueParam(
            v.name
          , makeTypeEntity(v.resultType)
          , v.defaultValue map (_.expression)
          , v.isImplicit
        )
      ))
  )
}
