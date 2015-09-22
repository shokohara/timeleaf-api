package models

import play.api.data.Forms._
import play.api.data._
import play.api.data.format.{Formats, Formatter}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

case class SignUpForm(name: String)
case class PostForm(kind: String, text: String)
case class ProfileBirthdayForm(years: Int, months: Int, days: Int)
case class ProfileForm(name: String, bio: String, color: String)
case class RoomForm(name: String)
case class ChangeRoomNameForm(id: Int, name: String)
case class ProfileForm2(name: String, sex: Option[Sex], prefecture: Option[Prefecture], bio: Option[String])
case class UserPut(name: String, sex: Option[Sex], prefecture: Option[Prefecture], bio: Option[String], color: Option[String])
case class RoomPut(userId: Long, name: String, limit: Int, locked: Boolean, authorities:Seq[Long])
case class RoomAuthoritiesPut(authorities:Seq[Long])

case class FilterForm(notKnown: Boolean, male: Boolean, female: Boolean)

object Forms {
  case class Session(id: Long, password: String)
  val BIO_MIN = 1
  val BIO_MAX = 255
  val MIN_MONTHES = 1
  val MAX_MONTHES = 12
  val RoomNameMaxLength = 1000
  val SKYPE_ID_MIN = 6
  val SKYPE_ID_MAX = 32
  val SKYPE_ID_PATTERN = s"[a-zA-Z0-9.,-_]{$SKYPE_ID_MIN,$SKYPE_ID_MAX}".r

  def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])
      (key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
    Formats.stringFormat.bind(key, data).right.flatMap { s =>
      scala.util.control.Exception.allCatch[T]
          .either(parse(s))
          .left.map(e => Seq(FormError(key, errMsg, errArgs)))
    }
  }

  implicit def sexFormat: Formatter[Sex] = new Formatter[Sex] {

    override val format = Some(("format.sex", Nil))

    def bind(key: String, data: Map[String, String]) = parsing(Sex.toSex, "error.sex", Nil)(key, data)

    def unbind(key: String, value: Sex) = Map(key -> value.toString)
  }

  implicit def prefectureFormat: Formatter[Prefecture] = new Formatter[Prefecture] {

    override val format = Some(("format.prefecture", Nil))

    def bind(key: String, data: Map[String, String]) = parsing(Prefecture.toPrefecture, "error.prefecture", Nil)(key, data)

    def unbind(key: String, value: Prefecture) = Map(key -> value.toString)
  }

  lazy val sexCheckConstraint: Constraint[Sex] = Constraint("constraints.sex") { plainText =>
    val errors = plainText match {
      case Male | Female => Nil
      case _ => Seq(ValidationError("Password is all letters"))
    }
    if (errors.isEmpty) Valid else Invalid(errors)
  }

  lazy val sexCheck: Mapping[Sex] = of[Sex].verifying(sexCheckConstraint)

  lazy val prefectureCheckConstraint: Constraint[Prefecture] = Constraint("constraints.prefecture") { plainText =>
    val errors = plainText match {
      case Hokkaido | Aomori | Iwate | Miyagi | Akita | Yamagata | Fukushima | Ibaraki | Tochigi | Gunma | Saitama | Chiba | Tokyo | Kanagawa | Niigata | Toyama | Ishikawa | Fukui | Yamanashi | Nagano | Gifu | Shizuoka | Aichi | Mie | Shiga | Kyoto | Osaka | Hyogo | Nara | Wakayama | Tottori | Shimane | Okayama | Hiroshima | Yamaguchi | Tokushima | Kagawa | Ehime | Kochi | Fukuoka | Saga | Nagasaki | Kumamoto | Oita | Miyazaki | Kagoshima | Okinawa => Nil
      case _ => Seq(ValidationError("Password is all letters"))
    }
    if (errors.isEmpty) Valid else Invalid(errors)
  }

  lazy val prefectureCheck: Mapping[Prefecture] = of[Prefecture].verifying(prefectureCheckConstraint)

  lazy val postForm = Form(single("text" -> nonEmptyText))

  lazy val searchForm = Form(single("text" -> optional(text())))

  lazy val session = Form(mapping(
                                   "id" -> longNumber,
                                   "password" -> nonEmptyText
                                 )(Session.apply)(Session.unapply))

  lazy val userCreateForm = Form(mapping(
                                          "name" -> nonEmptyText,
                                          "sex" -> optional(sexCheck),
                                          "prefecture" -> optional(prefectureCheck),
                                          "bio" -> optional(text(BIO_MIN, BIO_MAX))
                                        )(ProfileForm2.apply)(ProfileForm2.unapply))

  def userPutForm(user: User) = Form(mapping(
    "name" -> default(nonEmptyText, user.name),
    "sex" -> default(optional(sexCheck), user.sex),
    "prefecture" -> default(optional(prefectureCheck), user.prefecture),
    "bio" -> default(optional(nonEmptyText(BIO_MIN, BIO_MAX)), user.bio),
    "color" -> default(optional(nonEmptyText.verifying(colorCheckConstraint)), user.color)
  )(UserPut.apply)(UserPut.unapply))

  def roomPutForm(room: Room) = Form(mapping(
    "user_id" -> default(longNumber(1, Long.MaxValue), room.userId.get),
    "name" -> default(nonEmptyText(2, 20), room.name),
    "limit" -> default(number(2, 100), room.limit),
    "locked" -> default(boolean, room.locked),
    "authorities" -> seq(longNumber(1, Long.MaxValue))
  )(RoomPut.apply)(RoomPut.unapply))

  val roomAuthoritiesPutForm = Form(mapping(
    "authorities" -> seq(longNumber(1, Long.MaxValue))
  )(RoomAuthoritiesPut.apply)(RoomAuthoritiesPut.unapply))

  lazy val filterForm = Form(mapping(
                                      "not_known" -> boolean,
                                      "male" -> boolean,
                                      "female" -> boolean
                                    )(FilterForm.apply)(FilterForm.unapply))

  lazy val removePostForm = Form(single("id" -> longNumber))
  val NameMin = 1
  val NameMax = 10
  val TextMin = 1
  val TextMax = 255
  val BioMin = 1
  val BioMax = 255
  val COLOR_CODE_PATTERN = s"""[a-zA-Z0-9]{6}""".r
  val colorCheckConstraint: Constraint[String] = Constraint("constraints.colorcheck")({ code =>
    val errors = code match {
      case COLOR_CODE_PATTERN() => Nil
      case _ => Seq(ValidationError("Color code is invalid"))
    }
    if (errors.isEmpty) {
      Valid
    } else {
      Invalid(errors)
    }
  })

  val talkForm = Form(single("text" -> nonEmptyText))

//  val postForm = Form(mapping(
//    "kind" -> nonEmptyText,
//    "text" -> nonEmptyText
//  )(PostForm.apply)(PostForm.unapply))

  val signUpForm = Form(mapping(
    "name" -> nonEmptyText(NameMin, NameMax)
  )(SignUpForm.apply)(SignUpForm.unapply))

  val profileForm = Form(mapping(
    "name" -> nonEmptyText(NameMin, NameMax),
    "bio" -> text,
    "color" -> nonEmptyText.verifying(colorCheckConstraint)
  )(ProfileForm.apply)(ProfileForm.unapply))

  val roomForm = Form(mapping("name" -> nonEmptyText)(RoomForm.apply)(RoomForm.unapply))

  val updateRoomNameForm = Form(mapping("id" -> number, "name" -> nonEmptyText(1, 20))(ChangeRoomNameForm.apply)(ChangeRoomNameForm.unapply))
}
