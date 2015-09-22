package co.chaberi.commons.json.writes

import co.chaberi.commons.json.User
import org.joda.time.DateTime
import play.api.libs.json.Json

case class Talk(t: kind.Talk, data: Talk.Data)extends Response
object Talk {
  case class Data(user: User, talk: Data.Talk)
  object Data {
    case class Talk(text: String, color: String, dateTime: DateTime)
    object Talk{
      implicit val writes = Json.writes[Talk]
    }
    implicit val writes = Json.writes[Data]
  }
  implicit val writes = Json.writes[Talk]
}
