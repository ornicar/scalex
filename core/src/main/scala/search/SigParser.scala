package scalex.search

import scalex.model._
import scala.util.parsing.combinator._

object SigParser extends RegexParsers {

  def apply(pattern: String): Either[String, RawTypeSig] = parseAll(typeEntities, pattern) match {
    case f: Failure => Left(f.toString)
    case Success(result, _) => Right(RawTypeSig(result))
  }

  private def typeEntities: Parser[List[TypeEntity]] = repsep(typeEntity, "=>")

  private def typeEntityList: Parser[List[TypeEntity]] = repsep(typeEntity, ",")

  private def typeEntity: Parser[TypeEntity] =
    tuple | function | parameterizedClass | unrealParameterizedClass | simpleClass | unrealClass

  private def unrealClass: Parser[SimpleClass] =
    """\w""".r ^^ { name => SimpleClass(name, false) }

  private def simpleClass: Parser[SimpleClass] =
    """\w{2,}""".r ^^ { name => SimpleClass(name, true) }

  private def unrealParameterizedClass: Parser[ParameterizedClass] =
    """\w""".r ~ "[" ~ typeEntityList ~ "]" ^^ {
      case name ~ "[" ~ tpes ~ "]" => ParameterizedClass(name, false, tpes)
    }

  private def parameterizedClass: Parser[ParameterizedClass] =
    """\w{2,}""".r ~ "[" ~ typeEntityList ~ "]" ^^ {
      case name ~ "[" ~ tpes ~ "]" => ParameterizedClass(name, true, tpes)
    }

  private def function: Parser[Fun] =
    "(" ~ typeEntities ~ ")" ^^ {
      case  "(" ~ tpes ~ ")" => Fun(tpes)
    }

  private def tuple: Parser[Tuple] =
    "(" ~ typeEntityList ~ ")" ^^ {
      case  "(" ~ tpes ~ ")" => Tuple(tpes)
    }

  private def literal: Parser[TypeEntity] =
    """[\w\[\]\(\)]+""".r ^^ { str => Other(str) }
}
