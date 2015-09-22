import models.Room
import org.joda.time.{DateTime, DateTimeZone}
import play.api.mvc._
import play.api.{Application, Logger}
import play.filters.gzip.GzipFilter
import scalikejdbc._

import scala.io.Source

object Global extends WithFilters(LoggingFilter, JsonFilter, OriginFilter, new GzipFilter()) {

  override def beforeStart(app: Application): Unit = {
    super.beforeStart(app)
    Logger.info("Application is starting...")
    DateTimeZone.setDefault(DateTimeZone.UTC)
    println(DB.getAllTableNames().isEmpty)
    if (DB.getAllTableNames().isEmpty) {
      Logger.debug("sql/create.sql")
      SQL(Source.fromFile("sql/create.sql").mkString).update().apply()(AutoSession)
      Room.create(Room(-0, None, None, None, "default", 100, false, Nil, Nil, Nil, DateTime.now, DateTime.now, None))(AutoSession)
    }
    println(DB.getAllTableNames().isEmpty)
  }

  override def onStart(app: Application) {
    super.onStart(app)
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info("Application shutdown...")
  }
}
