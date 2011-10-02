package ophir.search

import ophir.model.Def

case class Result(

  val data: Def

) {

  override def toString = data.toString
}
