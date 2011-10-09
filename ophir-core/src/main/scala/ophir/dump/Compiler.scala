/* NSC -- new Scala compiler -- Copyright 2007-2011 LAMP/EPFL */

package ophir.dump

import scala.tools.nsc._
import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model._
import scala.util.control.ControlThrowable
import scala.tools.nsc.reporters.Reporter
import scala.tools.nsc.util.NoPosition
import scala.tools.nsc.io.{ File, Directory }
import DocParser.Parsed

/** A documentation processor controls the process of generating Scala documentation, which is as follows.
  *
  * * A simplified compiler instance (with only the front-end phases enabled) is created, and additional
  *   ''sourceless'' comments are registered.
  * * Documentable files are compiled, thereby filling the compiler's symbol table.
  * * A documentation model is extracted from the post-compilation symbol table.
  * * A generator is used to transform the model into the correct final format (HTML).
  *
  * A processor contains a single compiler instantiated from the processor's `settings`. Each call to `document`
  * uses the same compiler instance with the same symbol table. In particular, this implies that the scaladoc site
  * obtained from a call to `run` will contain documentation about files compiled during previous calls to the same
  * processor's `run` method.
  *
  * @param reporter The reporter to which both documentation and compilation errors will be reported.
  * @param settings The settings to be used by the documenter and compiler for generating documentation.
  *
  * @author Gilles Dubochet */
class Compiler(val reporter: Reporter, val settings: doc.Settings) { processor =>
  /** The unique compiler instance used by this processor and constructed from its `settings`. */
  object compiler extends Global(settings, reporter) with interactive.RangePositions {
    override protected def computeInternalPhases() {
      phasesSet += syntaxAnalyzer
      phasesSet += analyzer.namerFactory
      phasesSet += analyzer.packageObjects
      phasesSet += analyzer.typerFactory
      phasesSet += superAccessors
      phasesSet += pickler
      phasesSet += refchecks
    }
    override def forScaladoc = true
  }

  /** Creates a scaladoc site for all symbols defined in this call's `files`, as well as those defined in `files` of
    * previous calls to the same processor.
    * @param files The list of paths (relative to the compiler's source path, or absolute) of files to document. */
  def makeUniverse(files: List[String]): Option[Universe] = {
    new compiler.Run() compile files
    if (reporter.hasErrors)
      return None

    val extraTemplatesToDocument: Set[compiler.Symbol] = {
      if (settings.docUncompilable.isDefault) Set()
      else {
        val uncompilable = new {
          val global: compiler.type = compiler
          val settings = processor.settings
        } with Uncompilable { }

        compiler.docComments ++= uncompilable.comments

        uncompilable.templates
      }
    }

    val modelFactory = (
      new { override val global: compiler.type = compiler }
        with ModelFactory(compiler, settings)
        with comment.CommentFactory
        with TreeFactory {
          override def templateShouldDocument(sym: compiler.Symbol) =
            extraTemplatesToDocument(sym) || super.templateShouldDocument(sym)
        }
    )

    modelFactory.makeModel
  }

  object NoCompilerRunException extends ControlThrowable { }

  val documentError: PartialFunction[Throwable, Unit] = {
    case NoCompilerRunException =>
      reporter.info(NoPosition, "No documentation generated with unsucessful compiler run", false)
    case _: ClassNotFoundException =>
      ()
  }

  /** Generate document(s) for all `files` containing scaladoc documenataion.
    * @param files The list of paths (relative to the compiler's source path, or absolute) of files to document. */
  def universe(files: List[String]): Universe =
    makeUniverse(files) getOrElse { throw NoCompilerRunException }
}
