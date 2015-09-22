package co.chaberi.commons.json.writes

import co.chaberi.commons.json.User
import play.api.libs.json.Json

case class Join(t: kind.Join, data: Join.Data) extends Response
object Join {
  case class Data(user: User, users: Seq[User])
  object Data {
    implicit val writes = Json.writes[Data]
  }
  implicit val writes = Json.writes[Join]
}
