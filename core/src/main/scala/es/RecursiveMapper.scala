package scalex
package es

import org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder
import org.elasticsearch.common.xcontent.XContentBuilder
import scala.collection.JavaConversions._

trait RecursiveMapper {

  def javaMap(m: Map[_, _]): java.util.Map[_, _] =
    mapAsJavaMap(m collectValues javaValue)

  def javaList(l: List[_]): java.util.List[_] =
    seqAsJavaList(l collect javaValue)

  def javaValue: PartialFunction[Any, Any] = {
    case Some(v)      ⇒ javaValue(v)
    case v: Map[_, _] ⇒ javaMap(v)
    case v: List[_]   ⇒ javaList(v)
    case v            ⇒ v.toString
  }

  def json(data: Map[String, _]) = {
    val o = jsonBuilder.startObject
    data filter {
      case (k, v) ⇒ javaValue isDefinedAt v
    } foreach {
      case (k, v) ⇒ o.field(k, javaValue(v))
    }
    o.endObject
  }
}
