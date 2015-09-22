package services

import controllers.AuthConfigImpl.Id
import models.{Room, User}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.json.JsValue
import scalikejdbc.DBSession

object RoomsWS {

  case class Join(roomId: Long, userId: Long)
  case class UpdateUser(oldUser: User, updatedUser: User)
  case class UpdateRoom(user: User, oldRoom: Room, updatedRoom: Room)
  case class Users()

  var rooms: List[RoomWS] = List.empty

  def join(roomId: Long, userId: Id)(implicit session: DBSession): (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    rooms find (_.id == roomId) match {
      case Some(room) =>
        room.join(userId)
      case None =>
        val roomWs = new RoomWS(roomId, Nil)
        rooms = rooms :+ roomWs
        roomWs.join(userId)
    }
  }

  def quit(roomId: Long, userId: Id): Unit = {
    rooms filter (_.id == roomId) flatMap (_.users) filter (_.userId == userId) flatMap (_.wss) flatMap (_.channel) foreach (_.eofAndEnd())
  }

  def updateRoomOwner(user: User, oldRoom: Room, room: Room)(implicit session: DBSession) =
    rooms.foreach(_.updateRoomOwner(user, oldRoom, room))

  def updateRoomName(user: User, oldRoom: Room, room: Room)(implicit session: DBSession) =
    rooms.foreach(_.updateRoomName(user, oldRoom, room))

  def updateRoomLimit(user: User, oldRoom: Room, room: Room)(implicit session: DBSession) =
    rooms.foreach(_.updateRoomLimit(user, oldRoom, room))

  def updateUser(x: UpdateUser)(implicit session: DBSession) = rooms.foreach(_.updateUser(x))

  def updateUserName(x: UpdateUser)(implicit session: DBSession) = rooms.foreach(_.updateUserName(x))
}
