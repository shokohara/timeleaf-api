package controllers

import java.security.SecureRandom

import com.github.nscala_time.time.Imports._
import jp.t2v.lab.play2.auth._
import models.LoginSession
import scalikejdbc.{AutoSession, DBSession}

import scala.annotation.tailrec
import scala.util.Random

class RDBIdContainer extends IdContainer[Long] {

  implicit def session = AutoSession

  private[this] val random = new Random(new SecureRandom())

  override def startNewSession(userId: Long, timeoutInSeconds: Int): AuthenticityToken = {
    val token = generate
    store(token, userId, timeoutInSeconds)
    token
  }

  override def remove(token: AuthenticityToken): Unit = LoginSession.delete(token)

  override def get(token: AuthenticityToken): Option[Long] = {
    LoginSession.read(token).flatMap {
      case s if s.timeout <= DateTime.now.getMillis =>
        // セッションタイムアウト時刻を過ぎている場合、DBセッションデータを削除してIDは返さない
        LoginSession.delete(s.id)
        None
      case s => Some(s.id)
    }
  }

  override def prolongTimeout(token: AuthenticityToken, timeoutInSeconds: Int): Unit = {
    get(token).foreach { id =>
      LoginSession.update(LoginSession(id, token, timeoutMillis(timeoutInSeconds), DateTime.now, DateTime.now))
    }
  }

  @tailrec
  private[this] def generate: AuthenticityToken = {
    val table = "abcdefghijklmnopqrstuvwxyz1234567890"
    val token = Stream.continually(random.nextInt(table.length)).map(table).take(64).mkString
    if (get(token).isDefined) generate else token
  }

  private[this] def store(token: AuthenticityToken, userId: Long, timeoutInSeconds: Int)(implicit s: DBSession): Unit = {
    val timeout = timeoutMillis(timeoutInSeconds)
    val function = if (LoginSession.read(userId).isDefined) LoginSession.update _ else LoginSession.create _
    function(LoginSession(userId, token, timeout, DateTime.now, DateTime.now))
  }

  private[this] def timeoutMillis(timeoutInSeconds: Int): Long = DateTime.now.getMillis + (timeoutInSeconds * 1000)
}
