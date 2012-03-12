package scalex
package dump

import scala.tools.nsc
import scala.collection._

import scalex.model.{ TypeEntity => ScalexTypeEntity, _ }
import scalex.dump.model.TypeEntity

/** This trait extracts all required information for documentation from compilation units */
class ModelFactory(g: nsc.Global, s: nsc.doc.Settings)
  extends nsc.doc.model.ModelFactory(g, s)
  with nsc.doc.model.comment.CommentFactory
  with nsc.doc.model.TreeFactory {

  import global._
  import definitions.{ ObjectClass, ScalaObjectClass, RootPackage, EmptyPackage, NothingClass, AnyClass, AnyValClass, AnyRefClass }

  /** Override to keep more informations about the types */
  override def makeType(aType: Type, inTpl: => TemplateImpl): TypeEntity = {

    val scalaTypeEntity = super.makeType(aType, inTpl)
    val name = scalaTypeEntity.name

    def appendTypes1(types: List[Type]) = types map appendType1

    def checkFunctionType(tpe: TypeRef): Boolean = {
      val TypeRef(_, sym, args) = tpe
      (args.length > 0) && (args.length - 1 <= definitions.MaxFunctionArity) &&
      (sym == definitions.FunctionClass(args.length - 1))
    }
    def appendType1(tpe: Type): ScalexTypeEntity = tpe match {
      case tp: TypeRef if checkFunctionType(tp) => Fun(name, appendTypes1(tp.args))
      case tp: TypeRef if definitions.isScalaRepeatedParamType(tp) => Repeated(name, appendType1(tp.args.head))
      case tp: TypeRef if definitions.isByNameParamType(tp) => ByName(name, appendType1(tp.args.head))
      case tp: TypeRef if definitions.isTupleTypeOrSubtype(tp) => Tuple(name, appendTypes1(tp.args))
      case TypeRef(pre, aSym, targs) =>
        val bSym = normalizeTemplate(aSym)
        val (className, isReal) =
          if (bSym.isNonClassType) (bSym.name, !bSym.isDeferred)
          else (makeTemplate(bSym).name, true)
        if (targs.isEmpty) SimpleClass(className.toString, isReal)
        else ParameterizedClass(name, className.toString, isReal, appendTypes1(targs))
      /* Refined types */
      case RefinedType(parents, defs) =>
        val ps = appendTypes1((if (parents.length > 1) parents filterNot (_ == ObjectClass.tpe) else parents))
        val refinements = defs.toList map (_.defString)
        Refined(name, ps, refinements)
      /* Eval-by-name types */
      case NullaryMethodType(result) => NullaryMethod(name, appendType1(result))
      /* Polymorphic types */
      case PolyType(tparams, result) => assert(tparams nonEmpty)
        def typeParamsToString(tps: List[Symbol]): String = if(tps isEmpty) "" else
          tps.map{tparam =>
            tparam.varianceString + tparam.name + typeParamsToString(tparam.typeParams)
          }.mkString("[", ", ", "]")
        Polymorphic(name, typeParamsToString(tparams), appendType1(result))
      case tpen => Other(name, tpen.toString)
    }

    TypeEntity(scalaTypeEntity, appendType1(aType))
  }
}
