package models

import play.api.data.validation.ValidationError
import play.api.libs.json._
import scalikejdbc.TypeBinder
import scalaz.NonEmptyList

abstract sealed trait Sex extends DBValue[Int] with JSValue[String] {
  val DB_VALUE: Int
}

case object Male extends Sex {
  val DB_VALUE = 1

  val JS_VALUE = "male"

  override def toString: String = JS_VALUE
}

case object Female extends Sex {
  val DB_VALUE = 2

  val JS_VALUE = "female"

  override def toString: String = JS_VALUE
}

object Sex {

  def toSex(string: String) = string match {
    case e if e == Male.toString => Male
    case e if e == Female.toString => Female
  }

  implicit val writes: Writes[Sex] = new Writes[Sex] {
    def writes(d: Sex): JsValue = {
      val num = d match {
        case Male => Male.toString
        case Female => Female.toString
      }
      JsString(num)
    }
  }

  implicit val reads = new Reads[Sex] {
    def reads(json: JsValue): JsResult[Sex] = json match {
      case JsString(d) if d == Male.toString => JsSuccess(Male)
      case JsString(d) if d == Female.toString => JsSuccess(Female)
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.sex"))))
    }
  }

  implicit val binder: TypeBinder[Sex] =
    TypeBinder.option(TypeBinder.int).map(_.flatMap(toSex).orNull[Sex])

  def toSex(int: Int) = int match {
    case x if x == Male.DB_VALUE => Some(Male)
    case x if x == Female.DB_VALUE => Some(Female)
    case _ => None
  }

  val sexes = List(Male, Female)

  val nelSexes = NonEmptyList(Male, Female)
}
