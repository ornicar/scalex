package ophir.dump

import ophir.model
import scala.tools.nsc.doc.Universe
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.doc.model._

class ModelFactory {

  def makeModel(universe: Universe): List[model.Model] = {

    val models: ListBuffer[model.Model] = ListBuffer()

    def gather(owner: DocTemplateEntity): Unit =
      for(m <- owner.members if m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == owner) {
        m match {
          case tpl: DocTemplateEntity => gather(tpl)
          case fun: Def if isValid(fun) => models += makeFunction(fun)
          case _ =>
        }
      }

    gather(universe.rootPackage)

    models.toList
  }

  private[this] def isValid(fun: Def) =
    fun.visibility.isPublic &&
    !fun.isImplicit &&
    !fun.isAbstract &&
    !fun.comment.isEmpty

  private[this] def makeFunction(fun: Def) =
    model.Function(
        fun.name
      , makeResultType(fun.resultType)
      , fun.qualifiedName
      , fun.inTemplate.qualifiedName
      , HtmlWriter.commentToHtml(fun.comment).toString
      , makeValueParams(fun.valueParams)
    )

  private[this] def makeResultType(t: TypeEntity) =
    model.TypeEntity(t.name)

  private[this] def makeValueParams(params: List[List[ValueParam]]) =
    params.map(vs =>
        vs.map(v =>
            model.ValueParam(
                v.name
              , makeResultType(v.resultType)
              , v.defaultValue map makeTreeEntity
              , v.isImplicit
            )
          )
  )

  private[this] def makeTreeEntity(te: TreeEntity) =
    model.TreeEntity(te.expression)
}
