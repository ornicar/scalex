package ornicar.scalex
package index

import scala.tools.nsc.CompilerCommand

class Command(arguments: List[String], settings: Settings) extends CompilerCommand(arguments, settings) {
  override def cmdName = "scalex"
  override def usageMsg = (
    createUsageMsg("where possible scalex", shouldExplain = false, x ⇒ x.isStandard && settings.isScalexSpecific(x.name)) +
    "\n\nStandard scalac options also available:" +
    createUsageMsg(x ⇒ x.isStandard && !settings.isScalexSpecific(x.name))
  )
}
