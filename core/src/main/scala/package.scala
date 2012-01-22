package object scalex
  extends scalaz.Identitys
  with scalaz.Equals
  with scalaz.MABs
  with scalaz.MAs
  with scalaz.Options
  with scalaz.Lists
  with scalaz.Booleans {

  /**
   * K combinator implementation
   * Provides oneliner side effects
   * See http://hacking-scala.posterous.com/side-effecting-without-braces
   */
  implicit def addKcombinator[A](any: A) = new {
    def kCombinator(sideEffect: A => Unit): A = {
      sideEffect(any)
      any
    }
    def ~(sideEffect: A => Unit): A = kCombinator(sideEffect)
  }

  com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers()

  RegisterScalazConversionHelpers()

}

import _root_.com.mongodb.casbah.commons.conversions.MongoConversionHelper
import org.bson.{ BSON, Transformer }
import scalaz._

object RegisterScalazConversionHelpers extends ScalazHelpers {
  def apply() = {
    println("Registering Scalaz Conversions.")
    super.register()
  }
}

object DeregisterScalazConversionHelpers extends ScalazHelpers {
  def apply() = {
    println("Unregistering Scalaz Conversions.")
    super.unregister()
  }
}

trait ScalazHelpers extends ScalazSerializer with ScalazDeserializer

trait ScalazSerializer extends MongoConversionHelper {

  private val encodeType = classOf[NonEmptyList[_]]

  /** Encoding hook for MongoDB To be able to persist Scalaz DateTime to MongoDB */
  private val transformer = new Transformer {
    println("Encoding a Scalaz NonEmptyList.")

    def transform(o: AnyRef): AnyRef = o match {
      case d: NonEmptyList[_] => d.list
      case _ => o
    }

  }

  override def register() = {
    println("Hooking up Scalaz serializer.")
    /** Encoding hook for MongoDB To be able to persist Scalaz DateTime to MongoDB */
    BSON.addEncodingHook(encodeType, transformer)
    super.register()
  }

  override def unregister() = {
    println("De-registering Scalaz serializer.")
    BSON.removeEncodingHooks(encodeType)
    super.unregister()
  }
}

trait ScalazDeserializer extends MongoConversionHelper {

  private val encodeType = classOf[List[_]]
  private val transformer = new Transformer {
    println("Decoding Scalaz NonEmptyList.")

    def transform(o: AnyRef): AnyRef = o match {
      case x :: xs => NonEmptyList(x, xs)
      case _ => o
    }
  }

  override def register() = {
    println("Hooking up Joda DateTime deserializer")
    /** Encoding hook for MongoDB To be able to read Scalaz DateTime from MongoDB's BSON Date */
    BSON.addDecodingHook(encodeType, transformer)
    super.register()
  }

  override def unregister() = {
    println("De-registering Joda DateTime deserializer.")
    BSON.removeDecodingHooks(encodeType)
    super.unregister()
  }
}
