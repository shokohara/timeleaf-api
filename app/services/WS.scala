package services

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json.JsValue

case class WS(
    id: Long,
    onStart: Concurrent.Channel[JsValue] => Unit,
    onComplete: Unit => Unit,
    onError: Unit => Unit,
    f: JsValue => Unit,
    done: Unit => Any,
    onDisconnect: Long => Any) {
  var channel: Option[Concurrent.Channel[JsValue]] = None
  val ws: (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    val enumerator = Concurrent.unicast[JsValue](c => {
      Logger.info("onStart")
      channel = Some(c)
      onStart(c)
    }, {
      Logger.info("onComplete")
      onDisconnect(id)
      channel = None
      onComplete()
    }, (s, i) => {
      Logger.info("onError")
      onDisconnect(id)
      channel = None
      onError()
    })
    val iteratee: Iteratee[JsValue, Unit] = Iteratee.foreach[JsValue] { jsValue =>
      Logger.info("foreach")
      f(jsValue)
    } map { _ =>
      Logger.info("map")
      onDisconnect(id)
      channel = None
      done()
    }
    (iteratee, enumerator)
  }
}
