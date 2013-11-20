package org.scalex
package document

import model.{ Project, Seed, DocTemplate }

private[scalex] object ModelToDocument {

  def apply(d: model.Database): ScopedDocs = (d.seeds map { 
    case seed @ Seed(project, _) ⇒ project.id -> fromSeed(seed)
  }).toMap

  def fromSeed(seed: Seed): Docs = {

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
      projectId = seed.project.id,
      parent = parent,
      comment = o.comment,
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

    def makeVal(parent: Parent)(o: model.Member) = Val(
      member = makeMember(parent)(o))

    seed.root.templates flatMap walk(makeParent(seed.root))
  }
}
