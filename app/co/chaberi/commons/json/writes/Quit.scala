package co.chaberi.commons.json.writes

import co.chaberi.commons.json.User
import play.api.libs.json.Json

case class Quit(t: kind.Quit, data: Quit.Data) extends Response
object Quit {
  case class Data(user: User, users: Seq[User])
  object Data {
    implicit val writes = Json.writes[Data]
  }
  implicit val writes = Json.writes[Quit]
}
