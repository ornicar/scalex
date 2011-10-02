package ophir.dump

import ophir.model
import scala.tools.nsc.doc.Universe
import scala.collection.mutable
import scala.tools.nsc.doc.model._

class ModelFactory {

  def makeModel(universe: Universe): List[model.Model] = {

    val result = new mutable.HashMap[String,model.Model] {
      def addMember(d: MemberEntity): Unit = {
        d match {
          case d: Def if (!(this contains d.qualifiedName)) => update(d.qualifiedName, makeDef(d))
          case d => println(d)
        }
      }
    }

    val done = mutable.HashSet.empty[String]

    //def _gather(owner: DocTemplateEntity): Unit = {
      //for(m <- owner.members) m match {
        //case tpl: DocTemplateEntity  =>
          //if (!(done contains tpl.qualifiedName)) {
            //done += tpl.qualifiedName
            //for (method <- tpl.methods if isValid(method)) {
              //models += makeDef(method)
            //}
            ////for (method <- tpl.methods if !isValid(method)) {
              ////println(List(
                ////method, method.visibility.isPublic, method.isImplicit, method.isAbstract, method.inTemplate.isInstanceOf[Object]
              ////).mkString(" "))
            ////}
            //gather(tpl)
          //}
        //case _ =>
      //}
    //}

    def gather(owner: DocTemplateEntity): Unit =
      for(m <- owner.members if m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == owner)
        m match {
          case tpl: DocTemplateEntity =>
            result.addMember(tpl)
            gather(tpl)
          case alias: AliasType =>
            result.addMember(alias)
          case absType: AbstractType =>
            result.addMember(absType)
          case non: NonTemplateMemberEntity if !non.isConstructor =>
            result.addMember(non)
          case x @ _ =>
        }

    gather(universe.rootPackage)

    result.values.toList
  }

  private[this] def isValid(fun: Def) =
    fun.visibility.isPublic &&
    !fun.isImplicit &&
    !fun.isAbstract &&
    (
      fun.inTemplate.isInstanceOf[Trait]
      //|| fun.inTemplate.isInstanceOf[Object]
    )

  private[this] def makeDef(fun: Def) =
    model.Def(
        fun.name
      , makeQualifiedName(fun.qualifiedName)
      , makeParent(fun.inTemplate)
      , makeTypeEntity(fun.resultType)
      , HtmlWriter.commentToHtml(fun.comment).toString
      , makeValueParams(fun.valueParams)
      , makeTypeParams(fun.typeParams)
      , makeTokens(makeQualifiedName(fun.qualifiedName))
    )

  private[this] def makeQualifiedName(name: String): String =
    name.replace("scala.", "")

  private[this] def makeTokens(name: String): List[String] =
    model.Def.nameToTokens(name)

  private[this] def makeTypeParams(tps: List[TypeParam]): List[model.TypeParam] =
    tps map (tp =>
      model.TypeParam(
          tp.name
        , makeQualifiedName(tp.qualifiedName)
        , tp.variance
        , tp.lo map makeTypeEntity
        , tp.hi map makeTypeEntity
        , makeTypeParams(tp.typeParams)
      )
    )

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
