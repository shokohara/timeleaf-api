package co.chaberi.commons.json.writes.kind

import co.chaberi.commons.json.Values
import play.api.libs.json.{JsString, Writes}

sealed trait Kind {
  val t: String
}
object Kind {
  implicit val writes = Writes { x: Kind => JsString(x.t) }
}
case class Join() extends Kind {
  val t = Values.JOIN
}
case class Quit() extends Kind {
  val t = Values.QUIT
}
case class Talk() extends Kind {
  val t = Values.TALK
}
sealed trait UpdateRoom extends Kind
case class UpdateRoomOwner() extends UpdateRoom {
  val t = Values.UPDATE_ROOM_OWNER
}
case class UpdateRoomName() extends UpdateRoom {
  val t = Values.UPDATE_ROOM_NAME
}
case class UpdateRoomLimit() extends UpdateRoom {
  val t = Values.UPDATE_ROOM_LIMIT
}
sealed trait TypeStatus extends Kind
case class Typing() extends TypeStatus {
  val t = Values.TYPING
}
case class Typed() extends TypeStatus {
  val t = Values.TYPED
}
case class UpdateUserName() extends Kind {
  val t = Values.UPDATE_USER_NAME
}
case class UpdateUser() extends Kind {
  val t = Values.UPDATE_USER
}
case class Room() extends Kind {
  val t = Values.ROOM
}
