package scalex
package model

import com.novus.salat.annotations._

case class Def(

    /** Used as mongodb id */
    @Key("_id") id: String,

    /** See Entity */
    name: String,

    /** See Entity */
    qualifiedName: String,

    /** Function type signature */
    signature: String,

    /** Normalized function type signature */
    normSignature: String,

    /** Full unique function declaration */
    declaration: String,

    /** The function host class, trait or object */
    parent: Parent,

    /**
     * For members representing values: the type of the value returned by this member; for members
     * representing types: the type itself.
     */
    resultType: TypeEntity,

    /** The comment attached to this function, if any. */
    comment: Option[Comment],

    /**
     * The value parameters of this method. Each parameter block of a curried method is an element of the list.
     * Each parameter block is a list of value parameters.
     */
    valueParams: List[ValueParams],

    /** The type parameters of this entity. */
    typeParams: List[TypeParam],

    /** The package containing the def */
    pack: String,

    /** Some deprecation message if this function is deprecated, or none otherwise. */
    deprecation: Option[Block]) extends HigherKinded {

  /** Signature of the function parameters, not including the host class */
  def paramSignature: String = valueParams map (_.toString) mkString ""

  def toIndex = index.Def(
    id = id,
    qualifiedName = qualifiedNameWithoutScalaPrefix,
    signature = normSignature
  )

  def qualifiedNameWithoutScalaPrefix =
    if (qualifiedName startsWith "scala.") qualifiedName drop 6
    else qualifiedName

  def docUrl = "http://www.scala-lang.org/api/current/%s#%s".format(
    parent.qualifiedName.replace(".", "/"),
    urlFragment
  )

  def encodedDocUrl = "http://www.scala-lang.org/api/current/%s#%s".format(
    parent.qualifiedName.replace(".", "/"),
    UrlFragmentEncoder.encode(urlFragment)
  )

  def urlFragment: String = "%s%s%s:%s".format(
    name,
    showTypeParams,
    sugar {
      valueParams map (ps ⇒
        ps.params map (_.resultType.name) mkString ("(", ",", ")")
      ) mkString
    },
    resultType.toString
  )

  def sugar(str: String) = str.replace("=>", "⇒")

  override def toString = declaration
}
