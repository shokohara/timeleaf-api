package models

import play.api.data.validation.ValidationError
import play.api.libs.json._
import scalikejdbc.TypeBinder

sealed trait Role extends DBValue[Int] {
  val DB_VALUE: Int
}

object Role {

  implicit val writes: Writes[Role] = new Writes[Role] {
    def writes(d: Role): JsValue = JsNumber(d.DB_VALUE)
  }

  implicit val reads = new Reads[Role] {
    def reads(json: JsValue): JsResult[Role] = json match {
      case JsNumber(d) if d.toIntExact == Administrator.DB_VALUE => JsSuccess(Administrator)
      case JsNumber(d) if d.toIntExact == NormalUser.DB_VALUE => JsSuccess(NormalUser)
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.role"))))
    }
  }

  def toRole(int: Int): Role = int match {
    case x if x == Administrator.DB_VALUE => Administrator
    case x if x == NormalUser.DB_VALUE => NormalUser
  }

  implicit val binder: TypeBinder[Role] =
    TypeBinder.option(TypeBinder.string).map(_.flatMap(Json.parse(_).asOpt[Role]).orNull[Role])
}

case object Administrator extends Role {
  val DB_VALUE = 0
}

case object NormalUser extends Role {
  val DB_VALUE = 1
}
