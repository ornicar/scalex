package ornicar.scalex
package index

import scala.tools.nsc.doc.base.{ comment ⇒ nscComment }
import scala.tools.nsc.doc.Universe
import scala.tools.nsc.doc.{ model ⇒ nsc }

import model._

private[index] object Universer {

  def apply(universe: Universe): Database = new Database(entitiesOf(universe))

  private def entitiesOf(universe: Universe): List[DocTemplate] = {

    var seen = scala.collection.mutable.Set[nsc.DocTemplateEntity]()

    def extractEntities(tpl: nsc.DocTemplateEntity): List[nsc.DocTemplateEntity] = {
      seen += tpl
      tpl :: (tpl.templates collect {
        case t: nsc.DocTemplateEntity if (!seen(t)) ⇒ t
      } flatMap extractEntities)
    }
    extractEntities(universe.rootPackage).toList map Mapper.docTemplate
  }

  private object Mapper {

    def docTemplate(o: nsc.DocTemplateEntity): DocTemplate = DocTemplate(
      memberTemplate = memberTemplate(o),
      // inSource = o.inSource map { case (file, line) ⇒ (file.path, line) },
      sourceUrl = o.sourceUrl map (_.toString),
      members = o.members map member,
      templates = o.templates collect {
        case t: nsc.DocTemplateEntity ⇒ docTemplate(t)
      })

    def memberTemplate(o: nsc.MemberTemplateEntity) = MemberTemplate(
      template = template(o),
      higherKinded = higherKinded(o),
      valueParams = o.valueParams map (_ map valueParam),
      parentTypes = o.parentTypes map {
        case (tpl, typ) ⇒ TemplateAndType(template(tpl), typeEntity(typ))
      })

    def template(o: nsc.TemplateEntity) = Template(
      entity = entity(o),
      isPackage = o.isPackage,
      isRootPackage = o.isRootPackage,
      isTrait = o.isTrait,
      isClass = o.isClass,
      isObject = o.isObject,
      isDocTemplate = o.isDocTemplate,
      isCaseClass = o.isCaseClass,
      selfType = o.selfType map typeEntity)

    def member(o: nsc.MemberEntity): Member = Member(
      entity = entity(o),
      // comment = o.comment map comment,
      flags = o.flags collect {
        case nscComment.Paragraph(nscComment.Text(flag)) ⇒ flag
      },
      deprecation = o.deprecation.isDefined,
      migration = o.migration.isDefined,
      resultType = typeEntity(o.resultType),
      isDef = o.isDef,
      isVal = o.isVal,
      isLazyVal = o.isLazyVal,
      isVar = o.isVar,
      isConstructor = o.isConstructor,
      isAliasType = o.isAliasType,
      isAbstractType = o.isAbstractType,
      isAbstract = o.isAbstract,
      useCaseOf = o.useCaseOf map member,
      byConversion = o.byConversion map implicitConversion,
      signature = o.signature,
      signatureCompat = o.signatureCompat,
      isImplicitlyInherited = o.isImplicitlyInherited,
      isShadowedImplicit = o.isShadowedImplicit,
      isAmbiguousImplicit = o.isAmbiguousImplicit,
      isShadowedOrAmbiguousImplicit = o.isShadowedOrAmbiguousImplicit)

    def entity(o: nsc.Entity) = Entity(
      name = o.name,
      qualifiedName = o.qualifiedName,
      kind = o.kind)

    def typeEntity(o: nsc.TypeEntity) = o.name

    def comment(o: nscComment.Comment) = Comment(body = o.body)

    def implicitConversion(o: nsc.ImplicitConversion) = ImplicitConversion(
      source = docTemplate(o.source),
      targetType = typeEntity(o.targetType),
      targetTypeComponents = o.targetTypeComponents map {
        case (tpl, typ) ⇒ TemplateAndType(template(tpl), typeEntity(typ))
      },
      convertorMethod = o.convertorMethod.left map member,
      conversionShortName = o.conversionShortName,
      conversionQualifiedName = o.conversionShortName,
      convertorOwner = template(o.convertorOwner),
      members = o.members map member,
      isHiddenConversion = o.isHiddenConversion)

    def higherKinded(o: nsc.HigherKinded): HigherKinded = HigherKinded(
      typeParams = o.typeParams map typeParam
    )

    def typeParam(o: nsc.TypeParam): TypeParam = TypeParam(
      name = o.name,
      higherKinded = higherKinded(o),
      variance = o.variance,
      lo = o.lo map typeEntity,
      hi = o.lo map typeEntity)

    def valueParam(o: nsc.ValueParam) = ValueParam(
      name = o.name,
      resultType = typeEntity(o.resultType),
      defaultValue = o.defaultValue map (_.expression),
      isImplicit = o.isImplicit)
  }
}
