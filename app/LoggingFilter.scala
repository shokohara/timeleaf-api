import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._

object LoggingFilter extends EssentialFilter {
  def apply(nextFilter: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {
      val startTime = System.currentTimeMillis
      nextFilter(requestHeader).map { result =>
        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime
        val msg = s"id=${requestHeader.id} method=${requestHeader.method} uri=${requestHeader.uri} remote-address=${requestHeader.remoteAddress} status=${result.header.status} ${requestTime}ms"
        Logger.info(msg)
        result.withHeaders("Request-Id" -> requestHeader.id.toString)
      }
    }
  }
}
