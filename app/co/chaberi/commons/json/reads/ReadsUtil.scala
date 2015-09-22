package co.chaberi.commons.json.reads

import play.api.data.validation.ValidationError
import play.api.libs.json.Reads

object ReadsUtil {
  def equalReads[T](v: T)(implicit r: Reads[T]): Reads[T] =
    Reads.filter(ValidationError("validate.error.unexpected.value", v))(_ == v)
}
