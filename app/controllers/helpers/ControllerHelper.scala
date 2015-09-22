package controllers.helpers

import play.api.data.Form
import play.api.mvc.{Controller, Request, Result}

import scalaz._

trait ControllerHelper extends { self: Controller =>

  def toEither[T](orig: Form[T])(implicit req: Request[_]): \/[Result, T] = {
    def validationError(form: Form[_]): Result = BadRequest
    val form = orig.bindFromRequest()
    if (form.hasErrors) -\/(validationError(form)) else \/-(form.get)
  }
}
