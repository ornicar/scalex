package ornicar.scalex
package search
package document

private[search] object Extractor {

  def database(d: model.Database): ScopedDocs = d.projects map { p ⇒
    makeProject(p).id -> project(p)
  } toMap

  def project(p: model.Project): Docs = new ProjectExtractor(p).apply

  private final class ProjectExtractor(p: model.Project) {

    def apply = p.templates flatMap walk

    def walk(tpl: model.DocTemplate): Docs = 
      makeTemplate(tpl) ::
      tpl.templates.flatMap(walk) ::: 
      tpl.methods.map(makeDef) :::
      tpl.values.map(makeVal)

    def makeTemplate(o: model.DocTemplate) = Template(
      project = project,
      memberTemplate = o.memberTemplate)

    def makeDef(o: model.Def) = Def(
      project = project,
      member = o.member)

    def makeVal(o: model.Val) = Val(
      project = project,
      member = o.member)

    lazy val project = makeProject(p)
  }

  private def makeProject(p: model.Project) =
    Project(name = p.name, version = p.version)
}
