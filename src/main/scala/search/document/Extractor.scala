package ornicar.scalex
package search
package document

private[search] object Extractor {

  def apply(project: model.Project): Docs = new ProjectExtractor(project).apply

  private final class ProjectExtractor(project: model.Project) {

    lazy val projectDoc = Project(name = project.name, version = project.version)

    def apply = project.templates flatMap extract

    def extract(entity: Any): Docs = entity match {

      case docTemplate: model.DocTemplate ⇒ extract(docTemplate.methods)

      case method: model.Def              ⇒ makeDef(method) :: Nil
    }

    def makeDef(method: model.Def) = Def(
      project = projectDoc,
      qualifiedName = method.member.entity.qualifiedName
    )
  }
}
