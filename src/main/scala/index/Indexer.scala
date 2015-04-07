package org.scalex
package index

import scala.util.{ Try, Success, Failure }

import model._
import binary.ModelToBinary
import storage.{ BinaryToFile, HeaderFormat }

private[scalex] object Indexer {

  def apply(config: api.Index) {
    println("- source code -> nsc doc")

    val settings = new Settings()
    settings.embeddedDefaults[Settings]

    Project(config.name, config.version, config.scaladocUrl) flatMap { project ⇒
      SourceToNscDoc(config.name, config.version, settings, config.args) map { nscDoc ⇒
        println("- nsc doc -> model")
        val root = new NscDocToModel() docTemplate nscDoc
        val seed = Seed(project, root)
        val database = new Database(List(seed))
        println("- model -> binary")
        val bytes = ModelToBinary(database)
        println("- binary -> gziped file")
        val outputFile = new File(
          if (settings.outputFile.isDefault || settings.outputFile.value == ".")
            config.name + "_" + config.version + ".scalex"
          else settings.outputFile.value
        )
        BinaryToFile(outputFile, bytes, HeaderFormat write database.header)
        println("- Success!")
        println("- Database saved to " + outputFile.getAbsolutePath)
      }
    } match {
      case Failure(e) ⇒ throw e
      case Success(a) ⇒ a
    }
  }
}
