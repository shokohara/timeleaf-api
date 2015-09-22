package services

import co.chaberi.commons.json
import models.{Room, User}
import scalikejdbc.DBSession

import scala.language.implicitConversions

object Implicits {
  implicit def toUser(userWS: UserWS)(implicit session: DBSession): User = User.findById(userWS.userId).get
  implicit def toUser(user: User): json.User = json.User(
    id = user.id,
    name = user.name,
    bio = user.bio.getOrElse(""),
    color = user.color.getOrElse("000000"),
    image = user.image)
  implicit def toUsers(users: Seq[User])(implicit session: DBSession): Seq[json.User] = users.map(toUser)
  implicit def toRoom(roomWS: RoomWS)(implicit session: DBSession): Room = Room.findById(roomWS.id).get
  implicit def toRoom(room: Room): json.Room = json.Room(
    id = room.id,
    userId = room.userId,
    name = room.name,
    limit = room.limit,
    locked = room.locked,
    authorities = room.authorities
  )
}
