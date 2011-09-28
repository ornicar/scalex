package ophir.dump

import ophir.model._
import scala.tools.nsc.doc.Universe
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.doc.model.{DocTemplateEntity, Def}

class ModelFactory {

  def makeModel(universe: Universe): List[Model] = {

    val model: ListBuffer[Model] = ListBuffer()

    def gather(owner: DocTemplateEntity): Unit =
      for(m <- owner.members if m.inDefinitionTemplates.isEmpty || m.inDefinitionTemplates.head == owner) {
        m match {
          case tpl: DocTemplateEntity => gather(tpl)
          case fun: Def if isValid(fun) =>
            model += makeFunction(fun)
          case _ =>
        }
      }

    gather(universe.rootPackage)

    model.toList
  }

  private[this] def isValid(fun: Def) =
    fun.visibility.isPublic &&
    !fun.isImplicit &&
    !fun.isAbstract &&
    !fun.comment.isEmpty

  private[this] def makeFunction(fun: Def) =
    Function(
        fun.name
      , fun.qualifiedName
      , fun.inTemplate.qualifiedName
      , HtmlWriter.commentToHtml(fun.comment).toString
    )
}
