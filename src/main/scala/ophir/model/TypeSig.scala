package ophir.model

case class TypeSig(types: List[TypeEntity]) {

  override def toString = types mkString " => "

  def toIndex: String = (types map (_.toIndex)) mkString " => "
}
