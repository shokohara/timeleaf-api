package controllers

import controllers.helpers.ControllerHelper
import jp.t2v.lab.play2.auth._
import models._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._
import scalikejdbc._
import services.RoomsWS

object RoomAuthoritiesController extends Controller with ControllerHelper with LoginLogout with AuthConfigImpl with AuthElement {
  implicit def session = AutoSession

  def list(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val authorities = RoomUserAuthority.findAllBy(sqls.eq(RoomUserAuthority.defaultAlias.roomId, id))
    Ok(Json.toJson(authorities))
  }

  def update(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val oldRoom: Room = Room.findById(id).get
    Forms.roomAuthoritiesPutForm.bindFromRequest.fold(
      f => BadRequest,
      x => {
        RoomUserAuthority.findAllBy(sqls.eq(RoomUserAuthority.defaultAlias.roomId, oldRoom.id)).foreach(_.destroy())
        for (u <- x.authorities; a <- RoomAuthority.values) {
          RoomUserAuthority.create(RoomUserAuthority(-0, oldRoom.id, u, None, a, DateTime.now, DateTime.now))
        }
        val updatedRoom = Room.findById(oldRoom.id).get
        RoomsController.diff(oldRoom, updatedRoom) map {
          case UserId => RoomsWS.updateRoomOwner _
          case RoomName => RoomsWS.updateRoomName _
          case Limit => RoomsWS.updateRoomLimit _
          case RoomLocked => RoomsWS.updateRoomLimit _
          case RoomAuth => RoomsWS.updateRoomLimit _
        } foreach (_(loggedIn, oldRoom, updatedRoom))
        val authorities = RoomUserAuthority.findAllBy(sqls.eq(RoomUserAuthority.defaultAlias.roomId, id))
        Ok(Json.toJson(authorities))
      }
    )
  }
}
