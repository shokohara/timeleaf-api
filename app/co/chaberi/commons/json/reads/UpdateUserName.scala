package co.chaberi.commons.json.reads

import co.chaberi.commons.json.{Keys, Values}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class UpdateUserName(t: String, data: UpdateUserName.Data)
object UpdateUserName {
  case class Data(name: String)
  object Data {
    implicit val reads: Reads[Data] = Json.reads[Data]
  }
  implicit val reads: Reads[UpdateUserName] =
    ((__ \ Keys.T).read[String](ReadsUtil.equalReads(Values.UPDATE_USER_NAME)) and (__ \ Keys.DATA).read[Data])(UpdateUserName.apply _)
}
