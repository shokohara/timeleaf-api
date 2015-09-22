package controllers

import play.api.mvc._

object Application extends Controller {
  def options(unused: String) = Action(Ok)
}
