package org.scalex
package index

import scala.tools.nsc.CompilerCommand

private[index] final class Command(
    arguments: List[String],
    settings: Settings) extends CompilerCommand(arguments, settings) {

  override def files = super.files ++ {
    if (settings.inputDir.isDefault) Nil
    else findSources(new File(settings.inputDir.value)) map (_.getAbsolutePath)
  }

  private def findSources(dir: File): List[File] = dir.listFiles.toList flatMap { file ⇒
    if (file.isDirectory) findSources(file)
    else (file.getName endsWith ".scala") ?? List(file)
  }

  override def cmdName = "scalex"

  override def usageMsg = (
    createUsageMsg("where possible scalex", shouldExplain = false, x ⇒ x.isStandard && settings.isScalexSpecific(x.name)) +
    "\n\nStandard scalac options also available:" +
    createUsageMsg(x ⇒ x.isStandard && !settings.isScalexSpecific(x.name))
  )
}
