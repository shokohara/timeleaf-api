package controllers

import jp.t2v.lab.play2.auth.{AsyncIdContainer, AuthConfig}
import models.{User, Administrator, NormalUser, Role}
import play.api.mvc.{RequestHeader, Result, Results}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._

trait AuthConfigImpl extends AuthConfig with Results {

  type Id = Long

  type User = models.User

  type Authority = Role

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds: Int = 3600

  def resolveUser(id: Id)(implicit context: ExecutionContext): Future[Option[User]] = Future(User.findById(id))

  def loginSucceeded(request: RequestHeader)(implicit context: ExecutionContext): Future[Result] =
    Future.successful(Created)

  def logoutSucceeded(request: RequestHeader)(implicit context: ExecutionContext): Future[Result] =
    Future.successful(Ok)

  def authenticationFailed(request: RequestHeader)(implicit context: ExecutionContext): Future[Result] =
    Future.successful(Unauthorized)

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] =
    Future.successful(Unauthorized)

  def authorize(user: User, authority: Authority)(implicit context: ExecutionContext): Future[Boolean] = {
    Future.successful {
      (user.role, authority) match {
        case (Administrator, _) => true
        case (NormalUser, NormalUser) => true
        case _ => false
      }
    }
  }
  override lazy val idContainer: AsyncIdContainer[Long] = AsyncIdContainer(new RDBIdContainer())
}

object AuthConfigImpl extends AuthConfigImpl
