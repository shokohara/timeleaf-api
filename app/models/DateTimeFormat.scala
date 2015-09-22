package models

import org.joda.time.DateTime
import play.api.libs.json.{Reads, Writes}

trait DateTimeFormat {
  private val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  implicit val jodaDateTimeReads: Reads[DateTime] = Reads.jodaDateReads(dateFormat)
  implicit val jodaDateTimeWrites: Writes[DateTime] = Writes.jodaDateWrites(dateFormat)
}

object DateTimeFormat extends DateTimeFormat
