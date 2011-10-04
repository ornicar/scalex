package ophir.model

trait TypeEntityInterface

/** A type. Note that types and templates contain the same information only for the simplest types. For example, a type
  * defines how a template's type parameters are instantiated (as in `List[Cow]`), what the template's prefix is
  * (as in `johnsFarm.Cow`), and supports compound or structural types. */
case class TypeEntity (

  /** The human-readable representation of this type. */
  val name: String

  /** The types composing this type */

) extends TypeEntityInterface {
  /** The human-readable representation of this type. */
  override def toString = name
}

case class Class(val name: String, tparams: List[TypeEntityInterface]) extends TypeEntityInterface

case class Fun(val args: List[TypeEntityInterface]) extends TypeEntityInterface

case class Repeated(val arg: TypeEntityInterface) extends TypeEntityInterface

case class ByName(val arg: TypeEntityInterface) extends TypeEntityInterface

case class Tuple(val args: List[TypeEntityInterface]) extends TypeEntityInterface

case class Refined(val parents: List[TypeEntityInterface], val refinements: List[String]) extends TypeEntityInterface

case class NullaryMethod(val result: TypeEntityInterface) extends TypeEntityInterface

case class Polymorphic(val tparams: String, val result: TypeEntityInterface) extends TypeEntityInterface

case class Other(val text: String) extends TypeEntityInterface
