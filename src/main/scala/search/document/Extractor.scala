package ornicar.scalex
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
      entity = parent.memberTemplate.member.entity,
      typeParams = parent.memberTemplate.higherKinded.typeParams)

    def makeTemplate(parent: Parent)(o: DocTemplate) = Template(
      project = project,
      parent = parent,
      member = o.memberTemplate.member,
      role = o.memberTemplate.template.role,
      typeParams = o.memberTemplate.higherKinded.typeParams)

    def makeDef(parent: Parent)(o: model.Def) = Def(
      project = project,
      parent = parent,
      member = o.member,
      role = o.member.role,
      typeParams = o.higherKinded.typeParams,
      valueParams = o.valueParams)

    def makeVal(parent: Parent)(o: model.Val) = Val(
      project = project,
      parent = parent,
      role = o.member.role,
      member = o.member)

    lazy val project = makeProject(p)
  }

  private def makeProject(p: model.Project) =
    Project(name = p.name, version = p.version)
}
