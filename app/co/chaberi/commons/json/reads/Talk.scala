package co.chaberi.commons.json.reads

import play.api.libs.json._

case class Talk(user: Talk.User, talk: Talk.Data)
object Talk {
  case class Data(text: String, color: String)
  object Data {
    implicit val reads: Reads[Data] = Json.reads[Data]
  }
  case class User(name: String)
  object User {
    implicit val reads: Reads[User] = Json.reads[User]
  }
  implicit val reads: Reads[Talk] = Json.reads[Talk]
}
