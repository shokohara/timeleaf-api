package models

import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.{Right, Left, Either}

trait EnumLike {
  type ValueType
  def value: ValueType
}
trait Enum[A] extends EnumLike {
  type ValueType = A
}
trait EnumCompanion[E <: EnumLike] {
  def values: Seq[E]
  def valueOf(value: E#ValueType): Option[E] = values.find(_.value == value)
  def get(value: E#ValueType): E =
    valueOf(value).getOrElse(throw new IndexOutOfBoundsException(s"$value does not include."))
}
trait EnumFormatter[E <: EnumLike] {
  self: EnumCompanion[E] =>
  def toValue(str: String): Option[E#ValueType]

  implicit object EnumFormatterImpl extends Formatter[E] {
    override def bind(key: String, data: Map[String, String]): scala.Either[Seq[FormError], E] = {
      data.get(key).fold(error(key, s"Key($key) not found")) { arg =>
        toValue(arg).fold(error(key, s"Key($key) can't convert from string")) { stValue =>
          self.valueOf(stValue).fold(error(key, s"Value($stValue) not found"))(success)
        }
      }
    }
    override def unbind(key: String, value: E): Map[String, String] = Map(key -> value.value.toString)
  }

  private def error(key: String, message: String): Either[Seq[FormError], E] =
    Left(FormError(key, "No key found") :: Nil)
  private def success(st: E): Either[Seq[FormError], E] = Right(st)
}
