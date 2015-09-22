package co.chaberi.commons.json.writes

import models.User
import play.api.libs.json.Json

case class UpdateUserName(t: kind.UpdateUserName, data: UpdateUserName.Data) extends Response
object UpdateUserName {
  case class Data(oldUser: User, updatedUser: User, users: List[User])
  object Data {
    implicit val writes = Json.writes[Data]
  }
  implicit val writes = Json.writes[UpdateUserName]
}
