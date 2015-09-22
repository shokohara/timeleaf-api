package co.chaberi.commons.json.writes

import co.chaberi.commons.json.User
import models.Room
import play.api.libs.json.Json

case class UpdateRoom(t: kind.UpdateRoom, data: UpdateRoom.Data) extends Response
object UpdateRoom {
  case class Data(user: User, oldRoom: Room, room: Room, users: Seq[User])
  object Data {
    implicit val writes = Json.writes[Data]
  }
  implicit val writes = Json.writes[UpdateRoom]
}
