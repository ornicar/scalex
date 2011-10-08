package ophir.model

trait TypeSig {

  val types: List[TypeEntity]

  override def toString = types mkString " => "
}

case class RawTypeSig(types: List[TypeEntity]) extends TypeSig {

  /** Converts unreal class names to use the first alphabet letters */
  def normalize: NormalizedTypeSig = {

    /** unreal class names actually used */
    val currentLexicon = (types flatMap findLexicon).distinct

    /** class names to use instead */
    val lexicon = ('A' to 'Z') take currentLexicon.size map (_.toString)

    val replacements = (currentLexicon zip lexicon).toMap

    replaceLexicon(replacements)
  }

  private[this] def replaceLexicon(dict: Map[String, String]): NormalizedTypeSig =
    NormalizedTypeSig(types map (_.rename(dict)))

  /** Returns the list of all unreal class names */
  private[this] def findLexicon(tpe: TypeEntity): List[String] = {

    val childrenLexicon = tpe.children flatMap findLexicon

    tpe match {
      case cla: Class if !cla.isReal => cla.name :: childrenLexicon
      case _ => childrenLexicon
    }
  }
}

case class NormalizedTypeSig(types: List[TypeEntity]) extends TypeSig
