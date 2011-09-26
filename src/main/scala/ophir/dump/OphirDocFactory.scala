/* NSC -- new Scala compiler
 * Copyright 2007-2011 LAMP/EPFL
 * @author  David Bernard, Manohar Jonnalagedda
 */

package ophir.dump

import scala.tools.nsc.doc.{DocFactory,Settings}
import scala.tools.nsc.reporters.Reporter

import scala.tools.nsc.doc._
import scala.tools.nsc.doc.model
import scala.tools.nsc.interactive
import scala.tools.nsc.Global
import scala.tools.nsc.reporters.Reporter
import scala.tools.nsc.util.NoPosition
import scala.tools.nsc.io.{ File, Directory }
import DocParser.Parsed


class OphirDocFactory(val reporter: Reporter, val settings: Settings) { processor =>

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
  def document(files: List[String]): Unit = {
    val execution = new compiler.Run()
    execution compile files
    if (!reporter.hasErrors) {
      val modelFactory = (
        new { override val global: compiler.type = compiler }
          with model.ModelFactory(compiler, settings)
          with model.comment.CommentFactory
          with model.TreeFactory
      )
      val docModel = modelFactory.makeModel
      println("model contains " + modelFactory.templatesCount + " documentable templates")
      //println("Generating Scaladoc HTML")
      //(new html.HtmlFactory(docModel)) generate docModel
      //DataDumper.generate(docModel)
    }
  }
}
