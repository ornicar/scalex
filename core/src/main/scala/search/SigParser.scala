package scalex
package search

import scalex.model._
import scala.util.parsing.combinator._

object SigParser extends RegexParsers {

  class SyntaxException(msg: String) extends Exception(msg)

  def apply(pattern: String): Either[String, RawTypeSig] = parseAll(typeEntities, pattern) match {
    case f: Failure => Left(f.toString)
    case Success(result, _) => Right(RawTypeSig(result))
  }

  def typeEntities: Parser[List[TypeEntity]] = repsep(typeEntity, "=>")

  def typeEntityList: Parser[List[TypeEntity]] = repsep(typeEntity, ",")

  def typeEntityNel: Parser[List[TypeEntity]] = repsep(typeEntity, ",")

  def typeEntity: Parser[TypeEntity] =
    tuple | function | parameterizedClass | unrealParameterizedClass | simpleClass | unrealClass

  def unrealClass: Parser[SimpleClass] =
    """\w""".r ^^ { name => SimpleClass(name, false) }

  def simpleClass: Parser[SimpleClass] =
    """\w{2,}""".r ^^ { name => SimpleClass(name, true) }

  def unrealParameterizedClass: Parser[ParameterizedClass] =
    """\w""".r ~ "[" ~ typeEntityNel ~ "]" ^^ {
      case name ~ "[" ~ tpes ~ "]" => ParameterizedClass(name, false, tpes)
    }

  def parameterizedClass: Parser[ParameterizedClass] =
    """\w{2,}""".r ~ "[" ~ typeEntityNel ~ "]" ^^ {
      case name ~ "[" ~ tpes ~ "]" => ParameterizedClass(name, true, tpes)
    }

  def function: Parser[Fun] =
    "(" ~ typeEntities ~ ")" ^^ {
      case  "(" ~ tpes ~ ")" => Fun(tpes)
    }

  def tuple: Parser[Tuple] =
    "(" ~ typeEntityList ~ ")" ^^ {
      case  "(" ~ tpes ~ ")" => Tuple(tpes)
    }

  def literal: Parser[TypeEntity] =
    """[\w\[\]\(\)]+""".r ^^ { str => Other(str) }
}
