package scalex
package es

import org.elasticsearch.common.xcontent.XContentFactory._
import org.elasticsearch.common.xcontent.XContentBuilder
import scala.collection.JavaConversions._

import model._

object Mapper {

  def defToJson(d: Def) = obj(d) { (d, o) => o
    .field("name", d.name)
    .field("qualified", d.qualifiedName)
    .field("parent", map(d.parent) { p => Map(
      "name" -> p.name,
      "qualified" -> p.qualifiedName
    )})
  }

  def defToId(d: Def) = d.id

  def obj[A](model: A)(fun: (A, XContentBuilder) => XContentBuilder) = {
    val o = jsonBuilder.startObject
    fun(model, o)
    o.endObject
  }

  def map[A](model: A)(fun: A => Map[String, Any]) = mapAsJavaMap(fun(model))
}
