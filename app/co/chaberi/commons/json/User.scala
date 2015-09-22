package co.chaberi.commons.json

import play.api.libs.json.Json

case class User(id: Long, name: String, color: String, bio: String, image: Option[String])
object User {
  implicit val format = Json.format[User]
}
