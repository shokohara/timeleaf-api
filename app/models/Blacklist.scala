package models

import com.github.tototoshi.play.json.JsonNaming
import org.joda.time.DateTime
import play.api.libs.json._
import scalikejdbc._
import skinny.orm.{SkinnyCRUDMapper, SkinnyRecord}

case class Blacklist(
  id: Long,
  roomId: Long,
  userId: Long,
  user: Option[User] = None,
  operatorId: Long,
  operator: Option[User] = None,
  updatedAt: DateTime,
  createdAt: DateTime
) extends SkinnyRecord[Blacklist] {
  def skinnyCRUDMapper = Blacklist
  private def toColumns = Blacklist.withColumns { column =>
    column.roomId -> roomId ::
    column.userId -> userId ::
    column.operatorId -> operatorId :: Nil
  }
}

object Blacklist extends SkinnyCRUDMapper[Blacklist] {
  override lazy val tableName = "blacklist"
  implicit val format: Format[Blacklist] = JsonNaming.snakecase(Json.format[Blacklist])
  override val defaultAlias = createAlias("b")
  def create(blacklist: Blacklist)(implicit session: DBSession) =
    Blacklist.createWithNamedValues(blacklist.toColumns: _*)
  override def extract(rs: WrappedResultSet, rn: ResultName[Blacklist]): Blacklist =
    autoConstruct(rs, rn, "user", "operator")
  belongsTo[User](right = User, merge = (blacklist, user) => blacklist.copy(user = user)).byDefault
  belongsTo[User](right = User, merge = (blacklist, operator) => blacklist.copy(operator = operator)).byDefault
}
