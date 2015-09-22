package controllers.forms

import play.api.data.Form
import play.api.data.Forms._

object BlacklistUpdate {
  val form = Form(single("ids" -> list(longNumber(1, Long.MaxValue))))
}
