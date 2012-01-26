package scalex
package index

import com.novus.salat.annotations._

case class Def(

  /** Used as mongodb id */
    @Key("_id") id: String

  /** See Entity */
  , name: String

  /** See Entity */
  , qualifiedName: String

)
