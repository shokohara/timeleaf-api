package controllers

import java.net.URLDecoder
import java.nio.charset.Charset

import controllers.forms.BlacklistUpdate
import controllers.helpers.ControllerHelper
import jp.t2v.lab.play2.auth._
import models._
import org.joda.time.DateTime
import play.api.Play
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import scalikejdbc._
import services.Implicits._
import services.RoomsWS

import scala.util.Try
import scalaz._
import scalaz.Scalaz._
import scala.util.control.Exception._

object RoomsController extends Controller with ControllerHelper with LoginLogout with AuthConfigImpl with AuthElement {

  implicit def session = AutoSession

  lazy val timeout: Int = Play.maybeApplication.flatMap(_.configuration.getMilliseconds("room.timeout").map(_.toInt)).getOrElse(0)

  def decode(raw: String) = Try(URLDecoder.decode(raw, Charset.forName("utf8").name())).toOption

  def room(id: Long) = Room.findById(id)

  def wss(roomId: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      room <- room(roomId).toRightDisjunction(NotFound)
      _ <- Some(RoomsWS.rooms.filter(_.id == roomId).flatMap(_.users).length < room.limit).filter(_ == false)
        .map(_ => BadRequest(Json.toJson(ResultEnvelope("部屋の許容人数を超過しています" :: Nil)))).toLeftDisjunction(())
      _ <- Blacklist.findBy(
          sqls.eq(Blacklist.defaultAlias.roomId, roomId)
          .and.eq(Blacklist.defaultAlias.userId, loggedIn.id)
        ).map(_ => BadRequest(Json.toJson(ResultEnvelope("ブラックリストに登録されています" :: Nil)))).toLeftDisjunction(())
    } yield {
      Ok
    }).merge
  }

  def ws(roomId: Long) = WebSocket.tryAccept[JsValue] { implicit request =>
    (for {
      userEither <- authorized(NormalUser).map(\/.fromEither)
    } yield for {
      user <- userEither.map(_._1)
      room <- room(roomId).toRightDisjunction(NotImplemented)
      _ <- Some(RoomsWS.rooms.filter(_.id == roomId).flatMap(_.users).length < room.limit).filter(_ == false).map(_ => NotImplemented).toLeftDisjunction(())
      _ <- Blacklist.findBy(sqls.eq(Blacklist.defaultAlias.roomId, roomId).and.eq(Blacklist.defaultAlias.userId, user.id)).map(_ => NotImplemented).toLeftDisjunction(())
    } yield {
      RoomsWS.join(roomId, user.id)
    }).map(_.toEither)
  }

  def diff(u: Room, uu: Room): List[RoomChange] = (
    (if (u.userId == uu.userId) None else Some(UserId)) ::
    (if (u.name == uu.name) None else Some(RoomName)) ::
    (if (u.limit == uu.limit) None else Some(Limit)) ::
    (if (u.locked == uu.locked) None else Some(RoomLocked)) ::
    (if (u.authorities == uu.authorities) None else Some(RoomAuth)) :: Nil
  ).flatten

  def show(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    Ok(Json.toJson(Room.findById(id)))
  }

  def update(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val oldRoom: Room = Room.findById(id).get
    Forms.roomPutForm(oldRoom).bindFromRequest.fold(
      f => BadRequest,
      x => {
        RoomUserAuthority.findAllBy(sqls.eq(RoomUserAuthority.defaultAlias.roomId, oldRoom.id)).foreach(_.destroy())
        for (u <- x.authorities; a <- RoomAuthority.values) {
          RoomUserAuthority.create(RoomUserAuthority(-0, oldRoom.id, u, None, a, DateTime.now, DateTime.now))
        }
        val updatedRoom = {
          oldRoom.copy(userId = Some(x.userId), name = x.name, limit = x.limit, locked = x.locked).save()
          Room.findById(oldRoom.id).get
        }
        diff(oldRoom, updatedRoom) map {
          case UserId => RoomsWS.updateRoomOwner _
          case RoomName => RoomsWS.updateRoomName _
          case Limit => RoomsWS.updateRoomLimit _
          case RoomLocked => RoomsWS.updateRoomLimit _
          case RoomAuth => RoomsWS.updateRoomLimit _
        } foreach (_(loggedIn, oldRoom, updatedRoom))
        Ok(Json.toJson(updatedRoom).transform((__ \ 'password).json.prune).get)
      }
    )
  }

  def list = StackAction(AuthorityKey -> NormalUser) { request =>
    val rooms = RoomsWS.rooms.map(toRoom)
    Ok(Json.toJson(rooms))
  }

  def read_(id: Long) = StackAction(AuthorityKey -> NormalUser) { request =>
    val room: Option[Room] = RoomsWS.rooms.find(_.id == id).map(toRoom)
    Ok(Json.toJson(room))
  }
}
