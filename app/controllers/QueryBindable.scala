package controllers

import models.{Prefecture, Sex}
import play.api.mvc.QueryStringBindable

object QueryBindable {

  implicit def bindableSex = new QueryStringBindable[Sex] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Sex]] = {
      params(key).headOption.map { sexString =>
        try {
          Right(models.Sex.toSex(sexString))
        } catch {
          case e: MatchError => Left(e.getMessage())
        }
      }
    }

    def unbind(key: String, value: Sex) = s"sex=${value.toString}"
  }

  implicit def bindablePrefecture = new QueryStringBindable[Prefecture] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Prefecture]] = {
      params(key).headOption.map { sexString =>
        try {
          Right(models.Prefecture.toPrefecture(sexString))
        } catch {
          case e: MatchError => Left(e.getMessage())
        }
      }
    }

    def unbind(key: String, value: Prefecture) = s"prefecture=${value.toString}"
  }
}
