package scalex
package api

case class Index(
  name: String,
  dirs: List[File] = Nil) {
  def add(dir: File) = copy(dirs = dirs :+ dir)
  override def toString = "Index %s from %s".format(name, dirs mkString ",")
}

case class Search(words: List[String] = Nil) {
  def add(word: String) = copy(words = words :+ word)
  override def toString = "Search " + (words mkString " ")
}
