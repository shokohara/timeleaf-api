package models

import play.api.libs.json._
import scalikejdbc.TypeBinder

import scala.util.control.Exception._

sealed abstract class RoomAuthority(val value: Int) extends Enum[Int]
object RoomAuthority extends EnumCompanion[RoomAuthority] with EnumFormatter[RoomAuthority] {
  val values = RoomName :: RoomLimit :: RoomMember :: RoomLock :: Nil
  def toValue(string: String) = allCatch opt string.toInt
  implicit val reads = new Reads[RoomAuthority] {
    override def reads(json: JsValue): JsResult[RoomAuthority] = json match {
      case JsNumber(x) => valueOf(x.toInt).map(JsSuccess(_)).getOrElse(JsError())
      case _ => JsError("Int value expected")
    }
  }
  implicit val writes = new Writes[RoomAuthority] {
    override def writes(o: RoomAuthority): JsValue = JsNumber(o.value)
  }
  implicit val format: Format[RoomAuthority] = Format(reads, writes)
  implicit val binder: TypeBinder[RoomAuthority] = TypeBinder.int.map(x=>values.find(_.value == x).getOrElse(throw new IndexOutOfBoundsException(s"$x does not include.")))
  case object RoomName extends RoomAuthority(0)
  case object RoomLimit extends RoomAuthority(1)
  case object RoomMember extends RoomAuthority(2)
  case object RoomLock extends RoomAuthority(3)
}
