package org.scalex

package object document {

  type ProjectName = String
  type ProjectId = String

  type Docs = List[Doc]
  type ScopedDocs = Map[ProjectId, List[Doc]]
}
