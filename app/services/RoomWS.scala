package services

import controllers.AuthConfigImpl.Id
import models.{Room, User}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.json.JsValue
import scalikejdbc.DBSession
import services.Implicits._

case class RoomWS(id: Long, var users: List[UserWS]) {

  def room(implicit session: DBSession) = Room.findById(id).get
  def user(id: Id)(implicit session: DBSession) = User.findById(id).get

  def join(userId: Id)(implicit session: DBSession): (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    users find (_.userId == userId) match {
      case Some(user) =>
        user.connect
      case None =>
        val user = new UserWS(this, id, userId, Nil)
        users = users :+ user
        user.connect
    }
  }

  def updateRoomOwner(user: User, oldRoom: Room, room: Room)(implicit session: DBSession): Unit =
    this.users.foreach(_.updateRoomOwner(user, oldRoom, room, users.map(_.userId).map(this.user)))

  def updateRoomName(user: User, oldRoom: Room, room: Room)(implicit session: DBSession): Unit =
    this.users.foreach(_.updateRoomName(user, oldRoom, room, users.map(_.userId).map(this.user)))

  def updateRoomLimit(user: User, oldRoom: Room, room: Room)(implicit session: DBSession): Unit =
    this.users.foreach(_.updateRoomLimit(user, oldRoom, room, users.map(_.userId).map(this.user)))

  def updateUserName(x: RoomsWS.UpdateUser)(implicit session: DBSession): Unit = if (users.exists(_.userId == x.oldUser.id)) {
    users.foreach(_.updateUserName(x.updatedUser, x.oldUser, x.updatedUser, users.map(_.userId).map(user)))
  }

  def updateUser(x: RoomsWS.UpdateUser)(implicit session: DBSession): Unit = if (users.exists(_.userId == x.oldUser.id)) {
    users.foreach(_.updateUser(x.updatedUser, x.oldUser, x.updatedUser, users.map(_.userId).map(user)))
  }

  def connect(userWS: UserWS)(implicit session: DBSession): Unit = {
    try {
      userWS.room(room)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    users.foreach(_.join(UserWS.Join(user(userWS.userId), users.map(_.userId).map(user))))
    if (users.length == 1) {
      Room.findById(id).map(_.copy(userId = Some(userWS.userId)).save())
      users.foreach(_.updateRoomOwner(this.user(userWS.userId), room, room, users.map(_.userId).map(user)))
    }
  }

  def disconnect(id: Id)(implicit session: DBSession): Unit = {
    users = users.filterNot(_.wss.isEmpty)
    users.foreach(_.quit(UserWS.Quit(user(id), users.map(_.userId).map(user))))
    println(users.length)
    if (users.nonEmpty) {
      users.headOption.map(_.userId).map { ownerId =>
        Room.findById(id).map(_.copy(userId = Some(ownerId)).save())
      }
      users.foreach(_.updateRoomOwner(user(users.headOption.map(_.userId).head), room, room, users.map(_.userId).map(user)))
    }
  }
}
