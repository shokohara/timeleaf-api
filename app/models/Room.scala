package models

import com.github.tototoshi.play.json.JsonNaming
import org.joda.time.DateTime
import play.api.libs.json._
import scalikejdbc._
import skinny.orm.feature._
import skinny.orm.{SkinnyCRUDMapper, SkinnyRecord}

case class Room(
  id: Long,
  userId: Option[Long],
  user: Option[User] = None,
  password: Option[String],
  name: String,
  limit: Int,
  locked: Boolean,
  authorities: Seq[RoomUserAuthority] = Nil,
  blacklist: Seq[Blacklist] = Nil,
  whitelist: Seq[Whitelist] = Nil,
  updatedAt: DateTime,
  createdAt: DateTime,
  deletedAt: Option[DateTime] = None
) extends SkinnyRecord[Room] {
  def skinnyCRUDMapper = Room
  private def toColumns = Room.withColumns { column =>
    column.userId -> userId ::
    column.password -> password ::
    column.name -> name ::
    column.limit -> limit ::
    column.locked -> locked :: Nil
  }
}

object Room extends SkinnyCRUDMapper[Room] with TimestampsFeature[Room] with SoftDeleteWithTimestampFeature[Room] {
  override lazy val tableName = "room"
  override lazy val defaultAlias = createAlias("r")
  override val nameConverters = Map("^limit$" -> "limitt")
  implicit val format = JsonNaming.snakecase(Json.format[Room])
  def create(room: Room)(implicit session: DBSession) = Room.createWithNamedValues(room.toColumns: _*)
  override def extract(rs: WrappedResultSet, rn: ResultName[Room]): Room =
    autoConstruct(rs, rn, "user", "authorities", "blacklist", "whitelist")
  belongsTo[User](right = User, merge = (blacklist, user) => blacklist.copy(user = user)).byDefault
  hasMany[RoomUserAuthority](
    many = RoomUserAuthority -> RoomUserAuthority.defaultAlias,
    on = (r, a) => sqls.eq(r.id, a.roomId),
    merge = (room, authorities) => room.copy(authorities = authorities)
  ).byDefault
  hasMany[Blacklist](
    many = Blacklist -> Blacklist.defaultAlias,
    on = (r, b) => sqls.eq(r.id, b.roomId),
    merge = (room, blacklist) => room.copy(blacklist = blacklist)
  ).byDefault
//  hasMany[Whitelist](
//    many = Whitelist -> Whitelist.defaultAlias,
//    on = (r, w) => sqls.eq(r.id, w.roomId),
//    merge = (room, whitelist) => room.copy(whitelist = whitelist)
//  ).byDefault
}
