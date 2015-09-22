package controllers

import jp.t2v.lab.play2.auth._
import models.User
import models._
import org.joda.time.DateTime
import play.api.mvc._
import scalikejdbc.AutoSession

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.util.Random

object SessionController extends Controller with LoginLogout with AuthConfigImpl {

  implicit def session = AutoSession

//  def create = Action.async { implicit request =>
//    def success(form: Forms.Session) = DB.withTransaction { implicit session =>
//      Users.Table.filter(_.id === form.id).filter(_.password === form.password).firstOption
//    } flatMap (_.id) match {
//      case Some(id) => gotoLoginSucceeded(id)
//      case None => Future.successful(Unauthorized)
//    }
//    Forms.session.bindFromRequest.fold(hasErrors => Future.successful(BadRequest(hasErrors.errorsAsJson)), success)
//  }

  def create = Action.async { implicit request =>
    def success(form: Forms.Session) = User.findById(form.id).filter(_.password == form.password) match {
      case Some(u) => gotoLoginSucceeded(u.id)
      case None => Future.successful(Unauthorized)
    }
    Forms.session.bindFromRequest.fold(_ => {
      val now = DateTime.now
      val id = User.create(
        User(
          id = -0,
          role = NormalUser,
          password = "password",
          name = "ゲスト" + Random.nextInt(10) + "" + Random.nextInt(10) + "" + Random.nextInt(10),
          bio = None,
          sex = None,
          prefecture = None,
          color = None,
          image = None,
          createdAt = now,
          updatedAt = now
        )
      )
      val idCookie = Cookie(name = "id", value = id.toString, httpOnly = false)
      val passwordCookie = Cookie(name = "password", value = "password", httpOnly = false)
      val cookies = Array(idCookie, passwordCookie)
      gotoLoginSucceeded(id).map(_.withCookies(cookies: _*))
    }, success)
  }

  def delete = Action.async { implicit request =>
    gotoLogoutSucceeded
  }
}
