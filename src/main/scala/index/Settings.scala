package ornicar.scalex
package index

/**
 * An extended version of compiler settings, with additional scalex-specific options.
 * @param error A function that prints a string to the appropriate error stream
 * @param printMsg A function that prints the string, without any extra boilerplate of error
 */
private[index] final class Settings(
    error: String ⇒ Unit,
    pmsg: String ⇒ Unit = println(_)) extends scala.tools.nsc.doc.Settings(error, pmsg) {

  val inputDir = PathSetting(
    "-input-dir",
    "A directory in which all scala files will be indexed.",
    ""
  )

  val outputFile = PathSetting(
    "-output-file",
    "The file in which the binary database will be stored.",
    ""
  )

  // For improved help output.
  def scalexSpecific = Set[Settings#Setting](outputFile)

  val isScalexSpecific: String ⇒ Boolean = scalexSpecific map (_.name)
}
