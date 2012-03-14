package scalex
package index

case class Def(

    /** Matches the mongodb id */
    id: String,

    qualifiedName: String,

    signature: String,

    decSize: Int) {

  lazy val tokens: List[String] =
    qualifiedName.toLowerCase split Array('.', '#') toList

  lazy val pack: String = qualifiedName takeWhile { c ⇒
    c != '.' && c != '#'
  }

  override def toString = qualifiedName
}
