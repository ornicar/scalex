package scalex
package search

import com.github.ornicar.paginator.PaginatorLike

case class Results(paginator: PaginatorLike[index.Def], defs: List[model.Def]) {

}
