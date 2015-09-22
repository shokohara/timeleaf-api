package models

import com.github.tototoshi.play.json.JsonNaming
import org.joda.time.DateTime
import play.api.libs.json._
import scalikejdbc._
import skinny.orm.{SkinnyCRUDMapper, SkinnyRecord}

case class RoomUserAuthority(
  id: Long,
  roomId: Long,
  userId: Long,
  user: Option[User] = None,
  authority: RoomAuthority,
  updatedAt: DateTime,
  createdAt: DateTime
) extends SkinnyRecord[RoomUserAuthority] {
  def skinnyCRUDMapper = RoomUserAuthority
  private def toColumns = RoomUserAuthority.withColumns { column =>
    column.roomId -> roomId ::
    column.userId -> userId ::
    column.authority -> authority.value :: Nil
  }
}
object RoomUserAuthority extends SkinnyCRUDMapper[RoomUserAuthority] {
  override lazy val tableName = "room_user_authority"
  implicit val format: Format[RoomUserAuthority] = JsonNaming.snakecase(Json.format[RoomUserAuthority])
  override val defaultAlias = createAlias("rua")
  def create(room: RoomUserAuthority)(implicit session: DBSession) = RoomUserAuthority.createWithNamedValues(room.toColumns: _*)
  override def extract(rs: WrappedResultSet, rn: ResultName[RoomUserAuthority]): RoomUserAuthority =
    autoConstruct(rs, rn, "user")
  belongsTo[User](right = User, merge = (authority, user) => authority.copy(user = user)).byDefault
}
