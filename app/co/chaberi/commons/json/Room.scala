package co.chaberi.commons.json

import models.RoomUserAuthority
import play.api.libs.json.Json

case class Room(id: Long, userId: Option[Long], name: String, limit: Int, locked: Boolean, authorities: Seq[RoomUserAuthority])
object Room {
  implicit val writes = Json.writes[Room]
}
