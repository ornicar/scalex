package ophir.model

import com.novus.salat.annotations._

/** A type. Note that types and templates contain the same information only for the simplest types. For example, a type
  * defines how a template's type parameters are instantiated (as in `List[Cow]`), what the template's prefix is
  * (as in `johnsFarm.Cow`), and supports compound or structural types. */
@Salat
trait TypeEntity {

  def toIndex = toString
}

trait Class extends TypeEntity {

  val wildcard = "ø"

  val name: String
}

object Class {

  def apply(name: String, isReal: Boolean, tparams: List[TypeEntity]): Class =
    if (tparams.isEmpty)
      SimpleClass(name, isReal)
    else
      ParameterizedClass(name, isReal, tparams)
}

case class SimpleClass(name: String, isReal: Boolean) extends Class {

  override def toString = name

  override def toIndex = if (isReal) name else wildcard
}

case class ParameterizedClass(name: String, isReal: Boolean, tparams: List[TypeEntity]) extends Class {

  assume(!tparams.isEmpty, "Use a Class if params are empty")

  private[this] def wrap(list: List[_]): String = list mkString ("[", ", ", "]")

  override def toString = name + wrap(tparams)

  override def toIndex = (if (isReal) name else wildcard) + wrap(tparams)
}

case class Fun(args: List[TypeEntity]) extends TypeEntity {

  assume(!args.isEmpty, "Any function needs args")

  private[this] def wrap(list: List[_]): String = list mkString ("(", " => ", ")")

  override def toString = wrap(args)

  override def toIndex = wrap(args map (_.toIndex))
}

case class Repeated(arg: TypeEntity) extends TypeEntity {

  override def toString = arg + "*"
}

case class ByName(arg: TypeEntity) extends TypeEntity {

  override def toString = "⇒ " + arg
}

case class Tuple(args: List[TypeEntity]) extends TypeEntity {

  override def toString = args mkString ("(", ", ", ")")
}

case class Refined(parents: List[TypeEntity], refinements: List[String]) extends TypeEntity

case class NullaryMethod(result: TypeEntity) extends TypeEntity

case class Polymorphic(tparams: String, result: TypeEntity) extends TypeEntity

case class Other(text: String) extends TypeEntity {

  override def toString = text
}
