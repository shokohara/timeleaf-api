package co.chaberi.commons.json.reads

import co.chaberi.commons.json.{Keys, Values}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class UpdateRoom(t: String, data: UpdateRoom.Data)
object UpdateRoom {
  case class Data(name: String)
  object Data {
    implicit val reads: Reads[Data] = Json.reads[Data]
  }
  implicit val reads: Reads[UpdateRoom] =
    ((__ \ Keys.T).read[String](ReadsUtil.equalReads(Values.UPDATE_ROOM)) and (__ \ Keys.DATA).read[Data])(UpdateRoom.apply _)
}
