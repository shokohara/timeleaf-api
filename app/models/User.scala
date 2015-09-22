package models

import com.github.tototoshi.play.json.JsonNaming
import org.joda.time.DateTime
import play.api.libs.json.Json
import scalikejdbc._
import skinny.orm._
import skinny.orm.feature._

case class User(
  id: Long,
  role: Role,
  password: String,
  name: String,
  sex: Option[Sex],
  prefecture: Option[Prefecture],
  bio: Option[String],
  image: Option[String],
  color: Option[String],
  createdAt: DateTime,
  updatedAt: DateTime,
  deletedAt: Option[DateTime] = None
) extends SkinnyRecord[User] {
  def skinnyCRUDMapper = User

  override def save()(implicit session: DBSession): User = {
    skinnyCRUDMapper.updateById(id).withNamedValues(toColumns: _*)
    this
  }

  private def toColumns = User.withColumns { column =>
    column.role -> role.DB_VALUE ::
    column.password -> password ::
    column.name -> name ::
    column.sex -> sex.map(_.DB_VALUE) ::
    column.prefecture -> prefecture.map(_.DB_VALUE) ::
    column.bio -> bio ::
    column.image -> image ::
    column.color -> color :: Nil
  }
}

object User extends SkinnyCRUDMapper[User] with TimestampsFeature[User] with SoftDeleteWithTimestampFeature[User] {
  override lazy val tableName = "user"
  override lazy val defaultAlias = createAlias("u")
  implicit val format = JsonNaming.snakecase(Json.format[User])
  def create(user: User)(implicit session: DBSession) = User.createWithNamedValues(user.toColumns: _*)
  override def extract(rs: WrappedResultSet, rn: ResultName[User]): User = autoConstruct(rs, rn)
}
