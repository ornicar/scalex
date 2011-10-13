package scalex.search

import scalex.model.Def
import scalex.db.DefRepo
import scalex.parser.SigParser

object Search {

  type Result = Either[String, Iterator[Def]]

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  def find(query: String): Result = query match {
    case mixedRegex("", tpe) => TypeEngine find tpe
    case mixedRegex(text, tpe) => MixedEngine find (text, tpe)
    case tpe if tpe contains "=>" => TypeEngine find tpe
    case text => TextEngine find text
  }

  object TextEngine {

    def find(text: String): Result =
      tokenize(text).right map DefRepo.findByTokens

    def tokenize(text: String): Either[String, List[String]] = Def nameToTokens text match {
      case Nil => Left("Empty text")
      case tokens => Right(tokens)
    }
  }

  object TypeEngine {

    def find(tpe: String): Result =
      normalize(tpe).right map DefRepo.findBySig

    def normalize(tpe: String): Either[String, String] =
      SigParser(tpe).right map (_.normalize.toString)
  }

  object MixedEngine {

    def find(text: String, tpe: String): Result = for {
      tokens <- (TextEngine tokenize text).right
      normalized <- (TypeEngine normalize tpe).right
    } yield DefRepo.findByTokensAndSig(tokens, normalized)
  }
}
