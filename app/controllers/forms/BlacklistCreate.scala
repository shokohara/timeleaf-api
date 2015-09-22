package controllers.forms

import play.api.data.Form
import play.api.data.Forms._

object BlacklistCreate {
  val form = Form(single("id" -> longNumber(1, Long.MaxValue)))
}
