package scalex.search

import scalex.model.Def
import scalex.db.DefRepo
import scalex.parser.SigParser
import com.github.ornicar.paginator.Paginator
import com.github.ornicar.paginator.adapter.SalatAdapter

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._

object Search {

  type Result = Either[String, Paginator[Def]]
  type MongoQuery = Either[String, MongoDBObject]

  private val mixedRegex = """^([^\:]*)\:\s(.+)$""".r

  def find(query: Query): Result =
    mongoQuery(query.string).right map { paginator(_, query.currentPage, query.maxPerPage) }

  def mongoQuery(queryString: String): MongoQuery = queryString match {
    case mixedRegex("", tpe) => TypeEngine find tpe
    case mixedRegex(text, tpe) => MixedEngine find (text, tpe)
    case tpe if tpe contains " => " => TypeEngine find tpe
    case text => TextEngine find text
  }

  private[this] def paginator(query: MongoDBObject, currentPage: Int, maxPerPage: Int) =
    new Paginator(DefRepo.paginatorAdapter(query), currentPage, maxPerPage)

  object TextEngine {

    def find(text: String): MongoQuery =
      tokenize(text).right map DefRepo.queryByTokens

    def tokenize(text: String): Either[String, List[String]] = Def nameToTokens text match {
      case Nil => Left("Empty query")
      case tokens => Right(tokens)
    }
  }

  object TypeEngine {

    def find(tpe: String): MongoQuery =
      normalize(tpe).right map DefRepo.queryBySig

    def normalize(tpe: String): Either[String, String] =
      SigParser(tpe).right map (_.normalize.toString)
  }

  object MixedEngine {

    def find(text: String, tpe: String): MongoQuery = for {
      tokens <- (TextEngine tokenize text).right
      normalized <- (TypeEngine normalize tpe).right
    } yield DefRepo.queryByTokensAndSig(tokens, normalized)
  }
}
