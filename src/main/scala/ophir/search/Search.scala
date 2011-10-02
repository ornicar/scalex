package ophir.search

class Search {

  def find(queryString: String): Iterator[Result] = {
    val query = Query(queryString)
    val results = Engine.find(query)

    results
  }
}
