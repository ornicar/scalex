package ophir.search

import ophir.model.Def

object Search {

  type Result = Either[String, Iterator[Def]]

  def find(queryString: String): Result = {
    val query = Query(queryString)
    val results = Engine.find(query)

    results
  }
}
