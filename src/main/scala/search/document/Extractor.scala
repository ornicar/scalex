package org.scalex
package search
package document

import model.DocTemplate

private[search] object Extractor {

  def database(d: model.Database): ScopedDocs = d.projects map { p ⇒
    makeProject(p).id -> project(p)
  } toMap

  def project(p: model.Project): Docs = new ProjectExtractor(p).apply

  private final class ProjectExtractor(p: model.Project) {

    def apply = p.root.templates flatMap walk(makeParent(p.root))

    def walk(parent: Parent)(tpl: DocTemplate): Docs =
      makeParent(tpl) |> { context ⇒
        makeTemplate(parent)(tpl) ::
          tpl.templates.flatMap(walk(context)) :::
          tpl.methods.map(makeDef(context)) :::
          tpl.values.map(makeVal(context))
      }

    def makeParent(parent: DocTemplate) = Parent(
      entity = parent.member.entity,
      role = parent.template.role,
      typeParams = parent.typeParams)

    def makeMember(parent: Parent)(o: model.Member) = Member(
      project = project,
      parent = parent,
      entity = o.entity,
      role = o.role,
      flags = o.flags,
      resultType = o.resultType)

    def makeTemplate(parent: Parent)(o: DocTemplate) = Template(
      member = makeMember(parent)(o.member) orRole o.template.role,
      typeParams = o.typeParams)

    def makeDef(parent: Parent)(o: model.Def) = Def(
      member = makeMember(parent)(o.member),
      typeParams = o.typeParams,
      valueParams = o.valueParams)

    def makeVal(parent: Parent)(o: model.Val) = Val(
      member = makeMember(parent)(o.member))

    lazy val project = makeProject(p)
  }

  private def makeProject(p: model.Project) =
    Project(name = p.name, version = p.version)
}
