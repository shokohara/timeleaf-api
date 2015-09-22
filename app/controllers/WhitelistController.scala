package controllers

import controllers.forms.{BlacklistUpdate, BlacklistCreate}
import controllers.helpers.ControllerHelper
import jp.t2v.lab.play2.auth._
import models.User
import models._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._
import services.RoomsWS
import scalikejdbc._

import scalaz.Scalaz._

object WhitelistController extends Controller with ControllerHelper with LoginLogout with AuthConfigImpl with AuthElement {
  implicit def session = AutoSession

  def create(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      userId <- toEither(BlacklistCreate.form)
      oldRoom <- Room.findById(id).toRightDisjunction(NotImplemented)
      user <- User.findById(userId).toRightDisjunction(NotImplemented)
    } yield {
      val oldRoom: Room = Room.findById(id).get
      Whitelist.findAllBy(sqls.eq(Whitelist.defaultAlias.roomId, id)).foreach(_.destroy())
      Whitelist.create(Whitelist(-0, id, userId, None, loggedIn.id, None, DateTime.now, DateTime.now))
      val room = Room.findById(id).get
      RoomsController.diff(oldRoom, room) map {
        case UserId => RoomsWS.updateRoomOwner _
        case RoomName => RoomsWS.updateRoomName _
        case Limit => RoomsWS.updateRoomLimit _
        case RoomLocked => RoomsWS.updateRoomLimit _
        case RoomAuth => RoomsWS.updateRoomLimit _
      } foreach (_(loggedIn, oldRoom, room))
      Ok
    }).toEither.merge
  }

  def list(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      _ <- Room.findById(id).toRightDisjunction(NotImplemented)
    } yield {
      val list = Whitelist.findAllBy(sqls.eq(Whitelist.defaultAlias.roomId, id))
      Ok(Json.toJson(list))
    }).toEither.merge
  }

  def update(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      userIds <- toEither(BlacklistUpdate.form)
      oldRoom <- Room.findById(id).toRightDisjunction(NotImplemented)
    } yield {
      Whitelist.deleteBy(sqls.eq(Whitelist.defaultAlias.roomId, id))
      userIds.map(userId => Whitelist(-0, id, userId, None, loggedIn.id, None, DateTime.now, DateTime.now)).foreach(Whitelist.create)
      val room = Room.findById(id).get
      RoomsController.diff(oldRoom, room) map {
        case UserId => RoomsWS.updateRoomOwner _
        case RoomName => RoomsWS.updateRoomName _
        case Limit => RoomsWS.updateRoomLimit _
        case RoomLocked => RoomsWS.updateRoomLimit _
        case RoomAuth => RoomsWS.updateRoomLimit _
      } foreach (_(loggedIn, oldRoom, room))
      Ok
    }).toEither.merge
  }

  def delete(id: Long, ids: List[Long]) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      oldRoom <- Room.findById(id).toRightDisjunction(NotImplemented)
      user <- User.findById(id).toRightDisjunction(NotImplemented)
    } yield {
      Whitelist.deleteBy(sqls.eq(Whitelist.defaultAlias.roomId, id).and.in(Whitelist.defaultAlias.userId, ids))
      val room = Room.findById(id).get
      RoomsController.diff(oldRoom, room) map {
        case UserId => RoomsWS.updateRoomOwner _
        case RoomName => RoomsWS.updateRoomName _
        case Limit => RoomsWS.updateRoomLimit _
        case RoomLocked => RoomsWS.updateRoomLimit _
        case RoomAuth => RoomsWS.updateRoomLimit _
      } foreach (_(loggedIn, oldRoom, room))
      Ok
    }).toEither.merge
  }
}
