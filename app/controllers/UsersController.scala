package controllers

import jp.t2v.lab.play2.auth._
import models.{User, NormalUser}
import play.api.libs.json._
import play.api.mvc._

object UsersController extends Controller with LoginLogout with AuthConfigImpl with AuthElement {

  def show(id: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    User.findById(id) map { user =>
      Json.toJson(user).transform((__ \ 'password).json.prune).get
    } match {
      case Some(json) => Ok(json)
      case None => NotFound
    }
  }

  def delete(id: Long) = TODO
}
