package ophir.search

import ophir.model.Def
import ophir.db.DefRepo

trait Engine {

  protected def makeResult(d: Def): Result = Result(d)
}

class TextEngine extends Engine {

  def find(text: String): Iterator[Result] = {
   val tokens = Def.nameToTokens(text)
    DefRepo findByTokens tokens map makeResult
  }
}

object Engine {

  val textRegex = """^([\w\s-:]+)$""".r

  def find(query: Query): Iterator[Result] = query.string match {
    case textRegex(text) => (new TextEngine) find text
  }
}
