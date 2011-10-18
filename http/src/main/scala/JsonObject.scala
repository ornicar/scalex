package scalex
package http

import scalex.model.{Def, Block}

object Builder {

  def apply(query: String, funs: List[Def]) = Map(
    "query" -> query.string,
    "results" -> paginator.currentPageResults map apply,
    "nbResults" -> paginator.nbResults,
    "page" -> paginator.currentPage,
    "nbPages" -> paginator.nbPages
  )

  def apply(fun: Def): Map = {

    def optionBlock(b: Option[Block]): Map =
      Map("html" -> (b map (_.html)), "txt" -> (b map (_.txt)))

    def block(b: Block): Map =
      Map("html" -> b.html, "txt" -> b.txt)

    Map(
      "name" -> fun.name,
      "qualifiedName" -> fun.qualifiedName,
      "parent" -> Map(
        "name" -> fun.parent.name,
        "qualifiedName" -> fun.parent.qualifiedName,
        "typeParams" -> fun.parent.showTypeParams
      ),
      "comment" -> (fun.comment map { com =>
        Map(
          "short" -> block(com.short),
          "body" -> block(com.body),
          "authors" -> (com.authors map block),
          "see" -> (com.see map block),
          "result" -> optionBlock(com.result),
          "throws" -> Map(com.throws map { case (k, v) => (k.replace("_", ".") -> block(v)) }),
          "typeParams" -> Map(com.typeParams map { case (k, v) => (k.replace("_", ".") -> block(v)) }),
          "valueParams" -> Map(com.valueParams map { case (k, v) => (k.replace("_", ".") -> block(v)) }),
          "version" -> optionBlock(com.version),
          "since" -> optionBlock(com.since),
          "todo" -> (com.todo map block),
          "note" -> (com.note map block),
          "example" -> (com.example map block),
          "constructor" -> optionBlock(com.constructor),
          "source" -> com.source
        )
      }),
      "typeParams" -> fun.showTypeParams,
      "resultType" -> fun.resultType,
      "valueParams" -> fun.paramSignature,
      "signature" -> fun.signature,
      "package" -> fun.pack
    )
  }
}
