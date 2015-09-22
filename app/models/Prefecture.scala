package models

import play.api.data.validation.ValidationError
import play.api.libs.json._
import scalikejdbc.TypeBinder

import scala.util.control.Exception._

abstract sealed trait Prefecture extends DBValue[String] {
  val NAME: String
}

object Prefecture {

  implicit val writes: Writes[Prefecture] = new Writes[Prefecture] {
    def writes(d: Prefecture): JsValue = JsString(d.DB_VALUE)
  }

  implicit val reads = new Reads[Prefecture] {
    def reads(json: JsValue): JsResult[Prefecture] = json match {
      case JsString(d) => toPrefectureOpt(d) map (JsSuccess(_)) getOrElse JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.prefecture"))))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.prefecture"))))
    }
  }

  implicit val binder: TypeBinder[Prefecture] =
    TypeBinder.option(TypeBinder.string).map(_.flatMap(toPrefectureOpt).orNull[Prefecture])

  def toPrefectureOpt(string: String): Option[Prefecture] = allCatch opt toPrefecture(string)

  def toPrefecture(string: String): Prefecture = string match {
    case e if e == Hokkaido.DB_VALUE => Hokkaido
    case e if e == Aomori.DB_VALUE => Aomori
    case e if e == Iwate.DB_VALUE => Iwate
    case e if e == Miyagi.DB_VALUE => Miyagi
    case e if e == Akita.DB_VALUE => Akita
    case e if e == Yamagata.DB_VALUE => Yamagata
    case e if e == Fukushima.DB_VALUE => Fukushima
    case e if e == Ibaraki.DB_VALUE => Ibaraki
    case e if e == Tochigi.DB_VALUE => Tochigi
    case e if e == Gunma.DB_VALUE => Gunma
    case e if e == Saitama.DB_VALUE => Saitama
    case e if e == Chiba.DB_VALUE => Chiba
    case e if e == Tokyo.DB_VALUE => Tokyo
    case e if e == Kanagawa.DB_VALUE => Kanagawa
    case e if e == Niigata.DB_VALUE => Niigata
    case e if e == Toyama.DB_VALUE => Toyama
    case e if e == Ishikawa.DB_VALUE => Ishikawa
    case e if e == Fukui.DB_VALUE => Fukui
    case e if e == Yamanashi.DB_VALUE => Yamanashi
    case e if e == Nagano.DB_VALUE => Nagano
    case e if e == Gifu.DB_VALUE => Gifu
    case e if e == Shizuoka.DB_VALUE => Shizuoka
    case e if e == Aichi.DB_VALUE => Aichi
    case e if e == Mie.DB_VALUE => Mie
    case e if e == Shiga.DB_VALUE => Shiga
    case e if e == Kyoto.DB_VALUE => Kyoto
    case e if e == Osaka.DB_VALUE => Osaka
    case e if e == Hyogo.DB_VALUE => Hyogo
    case e if e == Nara.DB_VALUE => Nara
    case e if e == Wakayama.DB_VALUE => Wakayama
    case e if e == Tottori.DB_VALUE => Tottori
    case e if e == Shimane.DB_VALUE => Shimane
    case e if e == Okayama.DB_VALUE => Okayama
    case e if e == Hiroshima.DB_VALUE => Hiroshima
    case e if e == Yamaguchi.DB_VALUE => Yamaguchi
    case e if e == Tokushima.DB_VALUE => Tokushima
    case e if e == Kagawa.DB_VALUE => Kagawa
    case e if e == Ehime.DB_VALUE => Ehime
    case e if e == Kochi.DB_VALUE => Kochi
    case e if e == Fukuoka.DB_VALUE => Fukuoka
    case e if e == Saga.DB_VALUE => Saga
    case e if e == Nagasaki.DB_VALUE => Nagasaki
    case e if e == Kumamoto.DB_VALUE => Kumamoto
    case e if e == Oita.DB_VALUE => Oita
    case e if e == Miyazaki.DB_VALUE => Miyazaki
    case e if e == Kagoshima.DB_VALUE => Kagoshima
    case e if e == Okinawa.DB_VALUE => Okinawa
  }

  val prefectures = List(Hokkaido, Aomori, Iwate, Miyagi, Akita, Yamagata, Fukushima, Ibaraki, Tochigi, Gunma, Saitama, Chiba, Tokyo, Kanagawa, Niigata, Toyama, Ishikawa, Fukui, Yamanashi, Nagano, Gifu, Shizuoka, Aichi, Mie, Shiga, Kyoto, Osaka, Hyogo, Nara, Wakayama, Tottori, Shimane, Okayama, Hiroshima, Yamaguchi, Tokushima, Kagawa, Ehime, Kochi, Fukuoka, Saga, Nagasaki, Kumamoto, Oita, Miyazaki, Kagoshima, Okinawa)
}

case object Hokkaido extends Prefecture {
  val NAME = "北海道"

  val DB_VALUE = "JP-01"

  override def toString = NAME
}

case object Aomori extends Prefecture {
  val NAME = "青森県"

  val DB_VALUE = "JP-02"

  override def toString = NAME
}

case object Iwate extends Prefecture {
  val NAME = "岩手県"

  val DB_VALUE = "JP-03"

  override def toString = NAME
}

case object Miyagi extends Prefecture {
  val NAME = "宮城県"

  val DB_VALUE = "JP-04"

  override def toString = NAME
}

case object Akita extends Prefecture {
  val NAME = "秋田県"

  val DB_VALUE = "JP-05"

  override def toString = NAME
}

case object Yamagata extends Prefecture {
  val NAME = "山形県"

  val DB_VALUE = "JP-06"

  override def toString = NAME
}

case object Fukushima extends Prefecture {
  val NAME = "福島県"

  val DB_VALUE = "JP-07"

  override def toString = NAME
}

case object Ibaraki extends Prefecture {
  val NAME = "茨城県"

  val DB_VALUE = "JP-08"

  override def toString = NAME
}

case object Tochigi extends Prefecture {
  val NAME = "栃木県"

  val DB_VALUE = "JP-09"

  override def toString = NAME
}

case object Gunma extends Prefecture {
  val NAME = "群馬県"

  val DB_VALUE = "JP-10"

  override def toString = NAME
}

case object Saitama extends Prefecture {
  val NAME = "埼玉県"

  val DB_VALUE = "JP-11"

  override def toString = NAME
}

case object Chiba extends Prefecture {
  val NAME = "千葉県"

  val DB_VALUE = "JP-12"

  override def toString = NAME
}

case object Tokyo extends Prefecture {
  val NAME = "東京都"

  val DB_VALUE = "JP-13"

  override def toString = NAME
}

case object Kanagawa extends Prefecture {
  val NAME = "神奈川県"

  val DB_VALUE = "JP-14"

  override def toString = NAME
}

case object Niigata extends Prefecture {
  val NAME = "新潟県"

  val DB_VALUE = "JP-15"

  override def toString = NAME
}

case object Toyama extends Prefecture {
  val NAME = "富山県"

  val DB_VALUE = "JP-16"

  override def toString = NAME
}

case object Ishikawa extends Prefecture {
  val NAME = "石川県"

  val DB_VALUE = "JP-17"

  override def toString = NAME
}

case object Fukui extends Prefecture {
  val NAME = "福井県"

  val DB_VALUE = "JP-18"

  override def toString = NAME
}

case object Yamanashi extends Prefecture {
  val NAME = "山梨県"

  val DB_VALUE = "JP-19"

  override def toString = NAME
}

case object Nagano extends Prefecture {
  val NAME = "長野県"

  val DB_VALUE = "JP-20"

  override def toString = NAME
}

case object Gifu extends Prefecture {
  val NAME = "岐阜県"

  val DB_VALUE = "JP-21"

  override def toString = NAME
}

case object Shizuoka extends Prefecture {
  val NAME = "静岡県"

  val DB_VALUE = "JP-22"

  override def toString = NAME
}

case object Aichi extends Prefecture {
  val NAME = "愛知県"

  val DB_VALUE = "JP-23"

  override def toString = NAME
}

case object Mie extends Prefecture {
  val NAME = "三重県"

  val DB_VALUE = "JP-24"

  override def toString = NAME
}

case object Shiga extends Prefecture {
  val NAME = "滋賀県"

  val DB_VALUE = "JP-25"

  override def toString = NAME
}

case object Kyoto extends Prefecture {
  val NAME = "京都府"

  val DB_VALUE = "JP-26"

  override def toString = NAME
}

case object Osaka extends Prefecture {
  val NAME = "大阪府"

  val DB_VALUE = "JP-27"

  override def toString = NAME
}

case object Hyogo extends Prefecture {
  val NAME = "兵庫県"

  val DB_VALUE = "JP-28"

  override def toString = NAME
}

case object Nara extends Prefecture {
  val NAME = "奈良県"

  val DB_VALUE = "JP-29"

  override def toString = NAME
}

case object Wakayama extends Prefecture {
  val NAME = "和歌山県"

  val DB_VALUE = "JP-30"

  override def toString = NAME
}

case object Tottori extends Prefecture {
  val NAME = "鳥取県"

  val DB_VALUE = "JP-31"

  override def toString = NAME
}

case object Shimane extends Prefecture {
  val NAME = "島根県"

  val DB_VALUE = "JP-32"

  override def toString = NAME
}

case object Okayama extends Prefecture {
  val NAME = "岡山県"

  val DB_VALUE = "JP-33"

  override def toString = NAME
}

case object Hiroshima extends Prefecture {
  val NAME = "広島県"

  val DB_VALUE = "JP-34"

  override def toString = NAME
}

case object Yamaguchi extends Prefecture {
  val NAME = "山口県"

  val DB_VALUE = "JP-35"

  override def toString = NAME
}

case object Tokushima extends Prefecture {
  val NAME = "徳島県"

  val DB_VALUE = "JP-36"

  override def toString = NAME
}

case object Kagawa extends Prefecture {
  val NAME = "香川県"

  val DB_VALUE = "JP-37"

  override def toString = NAME
}

case object Ehime extends Prefecture {
  val NAME = "愛媛県"

  val DB_VALUE = "JP-38"

  override def toString = NAME
}

case object Kochi extends Prefecture {
  val NAME = "高知県"

  val DB_VALUE = "JP-39"

  override def toString = NAME
}

case object Fukuoka extends Prefecture {
  val NAME = "福岡県"

  val DB_VALUE = "JP-40"

  override def toString = NAME
}

case object Saga extends Prefecture {
  val NAME = "佐賀県"

  val DB_VALUE = "JP-41"

  override def toString = NAME
}

case object Nagasaki extends Prefecture {
  val NAME = "長崎県"

  val DB_VALUE = "JP-42"

  override def toString = NAME
}

case object Kumamoto extends Prefecture {
  val NAME = "熊本県"

  val DB_VALUE = "JP-43"

  override def toString = NAME
}

case object Oita extends Prefecture {
  val NAME = "大分県"

  val DB_VALUE = "JP-44"

  override def toString = NAME
}

case object Miyazaki extends Prefecture {
  val NAME = "宮崎県"

  val DB_VALUE = "JP-45"

  override def toString = NAME
}

case object Kagoshima extends Prefecture {
  val NAME = "鹿児島県"

  val DB_VALUE = "JP-46"

  override def toString = NAME
}

case object Okinawa extends Prefecture {
  val NAME = "沖縄県"

  val DB_VALUE = "JP-47"

  override def toString = NAME
}
