package controllers

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.Json

case class ResultEnvelope(errors: Seq[String])
object ResultEnvelope {
  implicit val format = JsonNaming.snakecase(Json.format[ResultEnvelope])
}
