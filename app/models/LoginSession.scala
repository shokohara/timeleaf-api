package models

import org.joda.time._
import scalikejdbc._
import skinny.orm._
import skinny.orm.feature._

case class LoginSession(
  id: Long,
  token: String,
  timeout: Long,
  createdAt: DateTime,
  updatedAt: DateTime
) extends SkinnyRecord[LoginSession] {
  def skinnyCRUDMapper = LoginSession
  private def toColumns = LoginSession.withColumns { column =>
    column.token -> token ::
    column.timeout -> timeout :: Nil
  }
}

object LoginSession extends SkinnyCRUDMapper[LoginSession] with TimestampsFeature[LoginSession] {
  override lazy val tableName = "session"
  override lazy val defaultAlias = createAlias("s")
  def create(loginSession: LoginSession)(implicit s: DBSession): Long = createWithNamedValues(loginSession.toColumns :_*)
  def read(id: Long)(implicit s: DBSession): Option[LoginSession] = findById(id)
  def read(token: String)(implicit s: DBSession): Option[LoginSession] = findBy(sqls.eq(defaultAlias.token, token))
  def update(loginSession: LoginSession)(implicit s: DBSession): Unit = loginSession.save()
  def delete(token: String)(implicit s: DBSession): Unit = findBy(sqls.eq(defaultAlias.token, token)).foreach(_.destroy())
  def delete(id: Long)(implicit s: DBSession): Unit = findById(id).foreach(_.destroy())
  override def extract(rs: WrappedResultSet, rn: ResultName[LoginSession]): LoginSession = autoConstruct(rs, rn)
}

//
//package models.slick
//
//import play.api.db.slick.Config.driver.simple._
//
//case class LoginSession(id: Long, token: String, timeout: Long)
//
//class LoginSessions(tag: Tag) extends Table[LoginSession](tag, LoginSessions.TableName) {
//  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
//  def token = column[String]("token")
//  def timeout = column[Long]("timeout")
//  def suUserId = foreignKey("su_" + LoginSessions.TableName + "_" + Users.TableName + "_id", id, Users.Table)(_.id)
//  def * = (id, token, timeout) <>((LoginSession.apply _).tupled, LoginSession.unapply)
//}
//
//object LoginSessions {
//  val TableName = "sessions"
//  val Table = TableQuery[LoginSessions]
//  def create(loginSession: LoginSession)(implicit s: Session): Long = Table returning Table.map(_.id) += loginSession
//  def read(id: Long)(implicit s: Session): Option[LoginSession] = Table.filter(_.id === id).firstOption
//  def read(token: String)(implicit s: Session): Option[LoginSession] = Table.filter(_.token === token).firstOption
//  def update(loginSession: LoginSession)(implicit s: Session): Unit =
//    Table.filter(_.id === loginSession.id).update(loginSession)
//  def delete(token: String)(implicit s: Session): Unit = Table.filter(_.token === token).delete
//  def delete(id: Long)(implicit s: Session): Unit = Table.filter(_.id === id).delete
//}
