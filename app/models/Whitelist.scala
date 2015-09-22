package models

import com.github.tototoshi.play.json.JsonNaming
import org.joda.time.DateTime
import play.api.libs.json._
import scalikejdbc._
import skinny.orm.{SkinnyCRUDMapper, SkinnyRecord}

case class Whitelist(
  id: Long,
  roomId: Long,
  userId: Long,
  user: Option[User] = None,
  operatorId: Long,
  operator: Option[User] = None,
  updatedAt: DateTime,
  createdAt: DateTime
) extends SkinnyRecord[Whitelist] {
  def skinnyCRUDMapper = Whitelist
  private def toColumns = Whitelist.withColumns { column =>
    column.roomId -> roomId ::
    column.userId -> userId ::
    column.operatorId -> operatorId :: Nil
  }
}

object Whitelist extends SkinnyCRUDMapper[Whitelist] {
  override lazy val tableName = "whitelist"
  implicit val format: Format[Whitelist] = JsonNaming.snakecase(Json.format[Whitelist])
  override val defaultAlias = createAlias("w")
  def create(blacklist: Whitelist)(implicit session: DBSession) =
    Whitelist.createWithNamedValues(blacklist.toColumns: _*)
  override def extract(rs: WrappedResultSet, rn: ResultName[Whitelist]): Whitelist =
    autoConstruct(rs, rn, "user", "operator")
//  hasOne[User](right = User, merge = (whitelist, user) => whitelist.copy(user = user)).byDefault
//  hasOne[User](right = User, merge = (whitelist, operator) => whitelist.copy(operator = operator)).byDefault
}
