import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{EssentialAction, EssentialFilter, RequestHeader}

object JsonFilter extends EssentialFilter {
  def apply(next: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {
      next(requestHeader).map { r =>
        //        r.header.headers.find(_ == CONTENT_TYPE -> JSON) match {
        //          case Some(_) => r.copy(body = Json.parse(r.body))
        //          case None => r
        //        }
        r
      }
    }
  }
}
