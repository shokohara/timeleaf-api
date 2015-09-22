package controllers

import controllers.forms.{BlacklistUpdate, BlacklistCreate}
import controllers.helpers.ControllerHelper
import jp.t2v.lab.play2.auth._
import models._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._
import scalikejdbc._
import services.RoomsWS

import scala.util.control.Exception._
import scalaz.Scalaz._

object BlacklistController extends Controller with ControllerHelper with LoginLogout with AuthConfigImpl with AuthElement {
  implicit def session = AutoSession

  def create(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      form <- toEither(BlacklistCreate.form)
      oldRoom <- Room.findById(id).toRightDisjunction(NotImplemented)
      user <- User.findById(form).toRightDisjunction(NotImplemented)
    } yield {
      RoomsWS.quit(id, form)
      allCatch toEither Blacklist.create(Blacklist(-0, id, form, None, loggedIn.id, None, DateTime.now, DateTime.now))
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
      val list = Blacklist.findAllBy(sqls.eq(Blacklist.defaultAlias.roomId, id))
      Ok(Json.toJson(list))
    }).toEither.merge
  }

  def update(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      userIds <- toEither(BlacklistUpdate.form)
      oldRoom <- Room.findById(id).toRightDisjunction(NotImplemented)
    } yield {
      Blacklist.deleteBy(sqls.eq(Blacklist.defaultAlias.roomId, id))
      userIds.map(userId => Blacklist(-0, id, userId, None, loggedIn.id, None, DateTime.now, DateTime.now)).foreach(Blacklist.create)
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
      Blacklist.deleteBy(sqls.eq(Blacklist.defaultAlias.roomId, id).and.in(Blacklist.defaultAlias.userId, ids))
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
