package co.chaberi.commons.json.writes

import models.Room
import org.joda.time.DateTime
import play.api.libs.json.Json

case class RoomInfo(t: kind.Room, data: Room) extends Response
object RoomInfo {
  implicit val writes = Json.writes[RoomInfo]
}
