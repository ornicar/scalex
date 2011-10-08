package ophir.parser

import ophir.model._
import scala.util.parsing.combinator._

object SigParser extends RegexParsers {

  def apply(pattern: String): RawTypeSig =
    RawTypeSig(parseAll(typeEntities, pattern).get)

  private def typeEntities: Parser[List[TypeEntity]] = repsep(typeEntity, "=>")

  private def typeEntity: Parser[TypeEntity] =
    function | parameterizedClass | unrealParameterizedClass | simpleClass | unrealClass

  private def unrealClass: Parser[SimpleClass] =
    """\w""".r ^^ { name => SimpleClass(name, false) }

  private def simpleClass: Parser[SimpleClass] =
    """\w{2,}""".r ^^ { name => SimpleClass(name, true) }

  private def unrealParameterizedClass: Parser[ParameterizedClass] =
    """\w""".r ~ "[" ~ typeEntities ~ "]" ^^ {
      case name ~ "[" ~ tpes ~ "]" => ParameterizedClass(name, false, tpes)
    }

  private def parameterizedClass: Parser[ParameterizedClass] =
    """\w{2,}""".r ~ "[" ~ typeEntities ~ "]" ^^ {
      case name ~ "[" ~ tpes ~ "]" => ParameterizedClass(name, true, tpes)
    }

  private def function: Parser[Fun] =
    "(" ~ typeEntities ~ ")" ^^ {
      case  "(" ~ tpes ~ ")" => Fun(tpes)
    }

  private def literal: Parser[TypeEntity] =
    """[\w\[\]\(\)]+""".r ^^ { str => Other(str) }
}
