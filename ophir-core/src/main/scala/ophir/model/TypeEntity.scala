package ophir.model

import com.novus.salat.annotations._

/** A type. Note that types and templates contain the same information only for the simplest types. For example, a type
  * defines how a template's type parameters are instantiated (as in `List[Cow]`), what the template's prefix is
  * (as in `johnsFarm.Cow`), and supports compound or structural types. */
@Salat
trait TypeEntity {

  type Dict = Map[String, String]

  def children: List[TypeEntity] = Nil

  def rename(dict: Dict): TypeEntity

  def substitute(dict: Dict, name: String) =
    if (dict contains name) dict(name) else name

  def substitute(dict: Dict, types: List[TypeEntity]) =
    types map (_.rename(dict))
}

trait Class extends TypeEntity {

  val name: String

  val isReal: Boolean
}

object Class {

  def apply(name: String, isReal: Boolean, tparams: List[TypeEntity]): Class =
    if (tparams.isEmpty) SimpleClass(name, isReal)
    else ParameterizedClass(name, isReal, tparams)
}

case class SimpleClass(name: String, isReal: Boolean) extends Class {

  override def toString = name

  def rename(dict: Dict): Class = copy(name = substitute(dict, name))
}

case class ParameterizedClass(name: String, isReal: Boolean, tparams: List[TypeEntity]) extends Class {

  assume(!tparams.isEmpty, "Use a Class if params are empty")

  private[this] def wrap(list: List[_]): String = list mkString ("[", ", ", "]")

  override def toString = name + wrap(tparams)

  override def children = tparams

  def rename(dict: Dict): ParameterizedClass = copy(name = substitute(dict, name), tparams = substitute(dict, tparams))
}

case class Fun(args: List[TypeEntity]) extends TypeEntity {

  assume(!args.isEmpty, "Any function needs args")

  private[this] def wrap(list: List[_]): String = list mkString ("(", " => ", ")")

  override def toString = wrap(args)

  override def children = args

  def rename(dict: Dict): Fun = copy(args = substitute(dict, args))
}

case class Repeated(arg: TypeEntity) extends TypeEntity {

  override def toString = arg + "*"

  override def children = List(arg)

  def rename(dict: Dict): Repeated = copy(arg = arg.rename(dict))
}

case class ByName(arg: TypeEntity) extends TypeEntity {

  override def toString = "=> " + arg

  override def children = List(arg)

  def rename(dict: Dict): ByName = copy(arg = arg.rename(dict))
}

case class Tuple(args: List[TypeEntity]) extends TypeEntity {

  override def toString = args mkString ("(", ", ", ")")

  override def children = args

  def rename(dict: Dict): Tuple = copy(args = substitute(dict, args))
}

case class Refined(parents: List[TypeEntity], refinements: List[String]) extends TypeEntity {

  override def children = parents

  def rename(dict: Dict): Refined = copy(parents = substitute(dict, parents))
}

case class NullaryMethod(result: TypeEntity) extends TypeEntity {

  override def children = List(result)

  def rename(dict: Dict): NullaryMethod = copy(result = result.rename(dict))
}

case class Polymorphic(tparams: String, result: TypeEntity) extends TypeEntity {

  override def children = List(result)

  def rename(dict: Dict): Polymorphic = copy(tparams = substitute(dict, tparams))
}

case class Other(text: String) extends TypeEntity {

  override def toString = text

  def rename(dict: Dict) = this
}
