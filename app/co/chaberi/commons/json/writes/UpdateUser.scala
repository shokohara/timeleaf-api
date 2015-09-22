package co.chaberi.commons.json.writes

import models.User
import play.api.libs.json.Json

case class UpdateUser(t: kind.UpdateUser, data: UpdateUserName.Data) extends Response
object UpdateUser {
  case class Data(oldUser: User, updatedUser: User, users: List[User])
  object Data {
    implicit val writes = Json.writes[Data]
  }
  implicit val writes = Json.writes[UpdateUser]
}
