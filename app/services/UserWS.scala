package services

import co.chaberi.commons.json
import co.chaberi.commons.json.{Keys, Values, reads, writes}
import controllers.AuthConfigImpl.Id
import models.{User, Room}
import org.joda.time.DateTime
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json.{JsValue, Json, Writes}
import scalikejdbc.DBSession
import services.Implicits._

case class UserWS(parent: RoomWS, roomId: Long, userId: Id, var wss: List[WS]) {

  def connect(implicit session: DBSession): (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    val id = wss.map(_.id).foldLeft(0L)(_ max _) + 1
    val ws = new WS(id, onStart, onComplete, onError, f, done, onDisconnect)
    wss = wss :+ ws
    ws.ws
  }

  def user(implicit session: DBSession) = User.findById(userId).get

  def user(userId: Long)(implicit session: DBSession) = User.findById(userId).get

  def join(x: UserWS.Join)(implicit session: DBSession) =
    push(writes.Join(writes.kind.Join(), writes.Join.Data(x.user, x.users)))
  def room(x: Room)(implicit session: DBSession) =
    push(writes.RoomInfo(json.writes.kind.Room(), x))
  def talk(u: User, text: String, now: DateTime)(implicit session: DBSession) =
    push(writes.Talk(json.writes.kind.Talk(), writes.Talk.Data(u, writes.Talk.Data.Talk(text, u.color.getOrElse("000000"), now))))
  def updateRoomOwner(user: User, oldRoom: Room, room: Room, users: Seq[User])(implicit session: DBSession) =
    push(writes.UpdateRoom(writes.kind.UpdateRoomOwner(), writes.UpdateRoom.Data(user, oldRoom, room, users)))
  def updateRoomName(user: User, oldRoom: Room, room: Room, users: Seq[User])(implicit session: DBSession) =
    push(writes.UpdateRoom(writes.kind.UpdateRoomName(), writes.UpdateRoom.Data(user, oldRoom, room, users)))
  def updateRoomLimit(user: User, oldRoom: Room, room: Room, users: Seq[User])(implicit session: DBSession) =
    push(writes.UpdateRoom(writes.kind.UpdateRoomLimit(), writes.UpdateRoom.Data(user, oldRoom, room, users)))
  def updateUserName(x: User, oldUser: User, updatedUser: User, users: List[User])(implicit session: DBSession) =
    push(writes.UpdateUserName(writes.kind.UpdateUserName(), writes.UpdateUserName.Data(oldUser, updatedUser, users)))
  def updateUser(x: User, oldUser: User, updatedUser: User, users: List[User])(implicit session: DBSession) =
    push(writes.UpdateUser(writes.kind.UpdateUser(), writes.UpdateUserName.Data(oldUser, updatedUser, users)))
  def typing(x: UserWS.Typing)(implicit session: DBSession) = push(writes.TypeStatus(writes.kind.Typing(), x.user))
  def typed(x: UserWS.Typed)(implicit session: DBSession) = push(writes.TypeStatus(writes.kind.Typed(), x.user))
  def quit(x: UserWS.Quit)(implicit session: DBSession) = push(writes.Quit(writes.kind.Quit(), writes.Quit.Data(toUser(x.user), toUsers(x.users))))

  def push[T](t: T)(implicit writes: Writes[T]) =
    wss.map(_.channel).collect { case Some(channel) => channel }.foreach(_ push Json.toJson(t))

  def onStart(l: Concurrent.Channel[JsValue])(implicit session: DBSession): Unit = parent.connect(this)
  def onComplete: Unit => Unit = _ => Unit
  def onError: Unit => Unit = _ => Unit
  def done: Unit => Unit = _ => Unit
  def onDisconnect(id: Long)(implicit session: DBSession): Unit = {
    wss = wss.filterNot(_.id == id)
    parent.disconnect(userId)
  }

  def f(event: JsValue)(implicit session: DBSession): Unit = {
    for {
      t <- (event \ Keys.T).validate[String] if t == Values.TALK
      x <- (event \ Keys.DATA).validate[reads.Talk]
    } yield {
      parent.users.foreach(_.talk(user, x.talk.text, DateTime.now))
    }
    for {
      t <- (event \ Keys.T).validate[String] if t == Values.TYPING
    } yield {
      parent.users.foreach(_.typing(UserWS.Typing(user(userId))))
    }
    for {
      t <- (event \ Keys.T).validate[String] if t == Values.TYPED
    } yield {
      parent.users.foreach(_.typed(UserWS.Typed(user(userId))))
    }
  }
}

object UserWS {
  case class Connected(iteratee: Iteratee[JsValue, Unit], enumerator: Enumerator[JsValue])
  case class Room(room: json.Room)
  case class Join(user: User, users: List[User])
  case class Typing(user: User)
  case class Typed(user: User)
  case class UpdateUserName(user: User, updatedUser: User, users: List[User])
  case class UpdateRoom(user: User, room: json.Room, users: List[User])
  case class UpdateRoomOwner(user: User, room: json.Room, users: List[User])
  case class UpdateRoomName(user: User, room: json.Room)
  case class UpdateRoomLimit(user: User, room: json.Room)
  case class Quit(user: User, users: List[User])
}
