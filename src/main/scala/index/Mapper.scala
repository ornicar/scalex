package org.scalex
package index

import scala.tools.nsc.doc.base.{ comment ⇒ nscComment }
import scala.tools.nsc.doc.{ model ⇒ nsc }

import model._

private[index] final class Mapper {

  var seen = scala.collection.mutable.Set[nsc.DocTemplateEntity]()

  def docTemplate(o: nsc.DocTemplateEntity): DocTemplate = {
    seen += o
    DocTemplate(
      template = template(o),
      member = member(o),
      typeParams = o.typeParams map typeParam,
      valueParams = o.valueParams map (_ map valueParam),
      parentTypes = o.parentTypes map (_._1.qualifiedName),
      // inSource = o.inSource map { case (file, line) ⇒ (file.path, line) },
      // sourceUrl = o.sourceUrl map (_.toString),
      members = filter(o.members) map member,
      templates = filter(o.templates) collect {
        case t: nsc.DocTemplateEntity ⇒ docTemplate(t)
      },
      methods = filter(o.methods) map method,
      values = filter(o.values) map member,
      abstractTypes = filter(o.abstractTypes) map abstractType,
      aliasTypes = filter(o.aliasTypes) map { a ⇒ typeEntity(a.alias) },
      primaryConstructor = o.primaryConstructor map constructor,
      constructors = filter(o.constructors) map constructor
    // companion = filter(o.companion) map docTemplate,
    // conversions = o.conversions map implicitConversion,
    // outgoingImplicitlyConvertedClasses = o.outgoingImplicitlyConvertedClasses map {
    // case (tpl, typ, imp) ⇒ (template(tpl), typeEntity(typ), implicitConversion(imp))
    // }
    )
  }

  def constructor(o: nsc.Constructor) = Constructor(
    member = member(o),
    valueParams = o.valueParams map2 valueParam)

  def abstractType(o: nsc.AbstractType) = AbstractType(
    member = member(o),
    typeParams = o.typeParams map typeParam,
    lo = o.lo map (_.name),
    hi = o.hi map (_.name))

  def method(o: nsc.Def) = Def(
    member = member(o),
    typeParams = o.typeParams map typeParam,
    valueParams = o.valueParams map2 valueParam)

  def template(o: nsc.TemplateEntity) = Template(
    entity = entity(o),
    role = if (o.isPackage) Role.Package
    else if (o.isObject) Role.Object
    else if (o.isTrait) Role.Trait
    else if (o.isCaseClass) Role.CaseClass
    else if (o.isClass) Role.Class
    else Role.Unknown,
    isDocTemplate = o.isDocTemplate,
    selfType = o.selfType map typeEntity)

  def member(o: nsc.MemberEntity): Member = Member(
    entity = entity(o),
    comment = o.comment map comment,
    inDefinitionTemplates = o.inDefinitionTemplates map (_.qualifiedName),
    flags = (o.flags collect {
      case nscComment.Paragraph(nscComment.Text(flag)) ⇒ flag
    }) ::: List(
      o.deprecation.isDefined option "deprecated",
      o.migration.isDefined option "migration"
    ).flatten,
    resultType = typeEntity(o.resultType),
    role = if (o.isDef) Role.Def
    else if (o.isVal) Role.Val
    else if (o.isLazyVal) Role.LazyVal
    else if (o.isVar) Role.Var
    else if (o.isConstructor) Role.Constructor
    else if (o.isAliasType) Role.AliasType
    else if (o.isAbstractType) Role.AbstractType
    else Role.Unknown,
    // byConversion = o.byConversion map implicitConversion,
    isImplicitlyInherited = o.isImplicitlyInherited)

  def entity(o: nsc.Entity) = Entity(
    qualifiedName = o.qualifiedName)

  def typeEntity(o: nsc.TypeEntity) = o.name

  def comment(o: nscComment.Comment) = Comment(
    body = body(o.body),
    summary = o.body.summary.isDefined ? inline(o.short) | Block("", ""),
    see = o.see map body,
    result = o.result map body,
    throws = o.throws.toMap mapValues body,
    valueParams = o.valueParams.toMap mapValues body,
    typeParams = o.typeParams.toMap mapValues body,
    version = o.version map body,
    since = o.since map body,
    todo = o.todo map body,
    deprecated = o.deprecated map body,
    note = o.note map body,
    example = o.example map body,
    constructor = o.constructor map body) 

  def implicitConversion(o: nsc.ImplicitConversion) = ImplicitConversion(
    source = docTemplate(o.source),
    targetType = typeEntity(o.targetType),
    targetTypeComponents = o.targetTypeComponents map (_._1.qualifiedName),
    convertorMethod = o.convertorMethod.left map member,
    conversionShortName = o.conversionShortName,
    conversionQualifiedName = o.conversionShortName,
    convertorOwner = template(o.convertorOwner),
    members = o.members map member,
    isHiddenConversion = o.isHiddenConversion)

  def typeParam(o: nsc.TypeParam): TypeParam = TypeParam(
    name = o.name,
    typeParams = o.typeParams map typeParam,
    variance = o.variance,
    lo = o.lo map typeEntity,
    hi = o.lo map typeEntity)

  def valueParam(o: nsc.ValueParam) = ValueParam(
    name = o.name,
    resultType = typeEntity(o.resultType),
    defaultValue = o.defaultValue map (_.expression),
    isImplicit = o.isImplicit)

  def filter[M[_]: scalaz.MonadPlus, A <: nsc.Entity](entities: M[A]): M[A] =
    implicitly[scalaz.MonadPlus[M]].filter(entities) {
      case t: nsc.DocTemplateEntity ⇒ !seen(t)
      case t: nsc.MemberEntity ⇒
        !(t.inDefinitionTemplates exists { tpl ⇒
          ignoredTemplates contains tpl.qualifiedName
        }) && !t.isShadowedOrAmbiguousImplicit
      case _ ⇒ false
    }

  def body(b: nscComment.Body): Block = html(Html bodyToHtml b)

  def inline(i: nscComment.Inline): Block = html(Html inlineToHtml i)

  def block(b: nscComment.Block): Block = html(Html blockToHtml b)

  def html(n: scala.xml.NodeSeq): Block = Block(n.toString, n.text)

  private def ignoredTemplates = Set(
    "scala.Any",
    "scala.AnyRef")
}
