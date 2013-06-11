package scalex
package index

import scala.util.Try

object Indexer {

  def apply(config: api.Index): Try[Unit] = for {
    name ← config.name.asTry(config.name.nonEmpty, badArg("Database name is empty"))
    dir ← validateDirs(config.dirs)
  } yield {
    println(config)
  }

  private def validateDirs(dirs: List[File]): Try[List[File]] =
    (dirs map { dir ⇒
      dir.asTry(dir.isDirectory, badArg(dir.getName + " is not a directory"))
    }).sequence

}
