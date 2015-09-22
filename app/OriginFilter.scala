import play.api.http.HeaderNames._
import play.api.http.HttpVerbs._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{EssentialAction, EssentialFilter, RequestHeader}

object OriginFilter extends EssentialFilter {
  def apply(next: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {
      next(requestHeader).map { r =>
        r.withHeaders(
          ACCESS_CONTROL_ALLOW_ORIGIN -> requestHeader.headers.get(ORIGIN).getOrElse(""),
          ACCESS_CONTROL_ALLOW_METHODS -> List(GET, POST, OPTIONS, DELETE, PUT).mkString(","),
          ACCESS_CONTROL_ALLOW_HEADERS -> CONTENT_TYPE,
          ACCESS_CONTROL_ALLOW_CREDENTIALS -> true.toString
        )
      }
    }
  }
}
