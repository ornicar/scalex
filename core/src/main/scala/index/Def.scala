package scalex
package index

case class Def(

  /** Matches the mongodb id */
    id: String

  /** See Entity */
  , name: String

  /** See Entity */
  , qualifiedName: String
) {

  lazy val tokens: List[String] =
    qualifiedName.toLowerCase split Array('.', '#') toList

  override def toString = qualifiedName
}
