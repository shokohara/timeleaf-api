package controllers

import controllers.forms.BlacklistCreate
import controllers.helpers.ControllerHelper
import jp.t2v.lab.play2.auth._
import models._
import play.api.mvc._
import scalikejdbc._
import services.RoomsWS

import scalaz.Scalaz._

object RoomUsersController extends Controller with ControllerHelper with LoginLogout with AuthConfigImpl with AuthElement {

  implicit def session = AutoSession

  def delete(roomId: Long, userId: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    (for {
      room <- Room.findById(roomId).toRightDisjunction(NotImplemented)
      user <- User.findById(userId).toRightDisjunction(NotImplemented)
    } yield {
      RoomsWS.quit(roomId, userId)
      Ok
    }).toEither.merge
  }
}
