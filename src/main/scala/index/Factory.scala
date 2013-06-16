package ornicar.scalex
package index

import scala.tools.nsc._
import scala.tools.nsc.doc._
import scala.util.control.ControlThrowable
import reporters.Reporter
import scala.reflect.internal.util.BatchSourceFile

/**
 * based on scala/src/scaladoc/scala/tools/nsc/doc/DocFactory.scala
 */
class Factory(val reporter: Reporter, val settings: Settings) { processor =>
  /** The unique compiler instance used by this processor and constructed from its `settings`. */
  object compiler extends ScalexGlobal(settings, reporter)

  /** Creates a scaladoc site for all symbols defined in this call's `source`,
    * as well as those defined in `sources` of previous calls to the same processor.
    * @param source The list of paths (relative to the compiler's source path,
    *        or absolute) of files to document or the source code. */
  def makeUniverse(source: Either[List[String], String]): Option[Universe] = {
    source match {
      case Left(files) =>
        new compiler.Run() compile files
      case Right(sourceCode) =>
        new compiler.Run() compileSources List(new BatchSourceFile("newSource", sourceCode))
    }

    if (reporter.hasErrors)
      return None

    val extraTemplatesToDocument: Set[compiler.Symbol] = {
      val uncompilable = new {
        val global: compiler.type = compiler
        val settings = processor.settings
      } with Uncompilable { }

      compiler.docComments ++= uncompilable.comments
      docdbg("" + uncompilable)

      uncompilable.templates

    }

    val modelFactory = (
      new { override val global: compiler.type = compiler }
        with model.ModelFactory(compiler, settings)
        with model.ModelFactoryImplicitSupport
        with model.ModelFactoryTypeSupport
        with model.diagram.DiagramFactory
        with model.CommentFactory
        with model.TreeFactory
        with model.MemberLookup {
          override def templateShouldDocument(sym: compiler.Symbol, inTpl: DocTemplateImpl) =
            extraTemplatesToDocument(sym) || super.templateShouldDocument(sym, inTpl)
        }
    )

    modelFactory.makeModel match {
      case Some(madeModel) =>
          println("model contains " + modelFactory.templatesCount + " documentable templates")
        Some(madeModel)
      case None =>
          println("no documentable class found in compilation units")
        None
    }
  }

  object NoCompilerRunException extends ControlThrowable { }

  val documentError: PartialFunction[Throwable, Unit] = {
    case NoCompilerRunException =>
      reporter.info(null, "No documentation generated with unsucessful compiler run", force = false)
    case _: ClassNotFoundException =>
      ()
  }

  /** Generate document(s) for all `files` containing scaladoc documenataion.
    * @param files The list of paths (relative to the compiler's source path, or absolute) of files to document. */
  def document(files: List[String]) {
    def generate() = {
      import doclet._
      val docletClass    = Class.forName(settings.docgenerator.value) // default is html.Doclet
      val docletInstance = docletClass.newInstance().asInstanceOf[Generator]

      docletInstance match {
        case universer: Universer =>
          val universe = makeUniverse(Left(files)) getOrElse { throw NoCompilerRunException }
          universer setUniverse universe

          docletInstance match {
            case indexer: Indexer => indexer setIndex model.IndexModelFactory.makeIndex(universe)
            case _                => ()
          }
        case _ => ()
      }
      docletInstance.generate()
    }

    try generate()
    catch documentError
  }

  private[index] def docdbg(msg: String) {
    if (settings.Ydocdebug)
      println(msg)
  }
}
