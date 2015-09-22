package co.chaberi.commons.json.writes

import models.User
import play.api.libs.json.Json

case class TypeStatus(t: kind.TypeStatus, data: User) extends Response
object TypeStatus {
  implicit val writes = Json.writes[TypeStatus]
}
