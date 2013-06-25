package org.scalex
package index

import scala.util.{ Try, Success, Failure }

import model._
import storage.binary.BinaryToFile
import storage.binary.ModelToBinary

private[scalex] object Indexer {

  def apply(config: api.Index) {
    println("- source code -> nsc doc")
    val settings = new Settings()
    Project(config.name, config.version) flatMap { project ⇒
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
        BinaryToFile(outputFile, bytes, database.header.toString)
        println("- Success!")
        println("- Database saved to " + outputFile.getAbsolutePath)
      }
    } match {
      case Failure(e) ⇒ throw e
      case Success(a) ⇒ a
    }
  }
}
