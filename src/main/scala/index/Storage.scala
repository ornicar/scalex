package ornicar.scalex
package index

import java.io._
import sbinary._
import Operations._

import model.Database

object IndexRepo extends DefaultProtocol {

  implicit val dbFormat = asProduct4(Def)(Def.unapply(_).get)

	implicit object DatabaseFormat extends Format[Database]{
		def reads(in : Input) = listFormat[DocTemplate]
			case 0 => Bin(reads(in), reads(in))
			case _ => Leaf(read[String](in))
		}

		def writes(out : Output, value : Database) = value match {
			case Bin(left, right) => write[Byte](out, 0); writes(out, left); writes(out, right)
			case Leaf(label) => write[Byte](out, 1); write(out, label)
		}
	}

  def write(file: File, db: Database) { toFile(db)(file) }

  def read(file: File): Database = { fromFile[Database](file) }
}
