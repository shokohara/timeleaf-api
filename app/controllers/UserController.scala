package controllers

import java.io.File
import javax.imageio.ImageIO

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest}
import com.amazonaws.{AmazonClientException, AmazonServiceException, ClientConfiguration}
import jp.t2v.lab.play2.auth._
import models._
import org.apache.commons.io.FileUtils
import org.imgscalr.Scalr
import org.joda.time.DateTime
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc._
import scalikejdbc.AutoSession
import services.RoomsWS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

object UserController extends Controller with LoginLogout with AuthConfigImpl with AuthElement {

  val ID = "id"
  val PASSWORD = "password"
  implicit def autoSession = AutoSession

  def create = Action.async { implicit request =>
    val password = "password"
    val now = DateTime.now
    val id = User.create(
      User(
        id = -0,
        role = NormalUser,
        password = "password",
        name = "ゲスト" + Random.nextInt(10) + "" + Random.nextInt(10) + "" + Random.nextInt(10),
        bio = None,
        sex = None,
        prefecture = None,
        color = None,
        image = None,
        createdAt = now,
        updatedAt = now
      )
    )
    val idCookie = Cookie(name = ID, value = id.toString, httpOnly = false)
    val passwordCookie = Cookie(name = PASSWORD, value = password, httpOnly = false)
    val cookies = Array(idCookie, passwordCookie)
    gotoLoginSucceeded(id).map(_.withCookies(cookies: _*))
  }

  def show = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    Ok(Json.toJson(loggedIn).transform((__ \ 'password).json.prune).get)
  }

  private def diff(u: User, uu: models.User): List[Change] = {
    (if (u.name == uu.name) None else Some(Name)) ::
    (if (u.sex == uu.sex) None else Some(Sex)) ::
    (if (u.prefecture == uu.prefecture) None else Some(Prefecture)) ::
    (if (u.bio == uu.bio) None else Some(Bio)) ::
    (if (u.image == uu.image) None else Some(Image)) ::
    (if (u.color == uu.color) None else Some(Color)) :: Nil flatten
  }

  def update = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    Forms.userPutForm(loggedIn).bindFromRequest.fold(
      f => BadRequest,
      x => {
        val oldUser = loggedIn
        val updatedUser = {
          loggedIn.copy(name = x.name, sex = x.sex, prefecture = x.prefecture, color = x.color, updatedAt = DateTime.now).save()
          User.findById(loggedIn.id).get
        }
        diff(oldUser, updatedUser) foreach {
          case Name => RoomsWS.updateUserName(RoomsWS.UpdateUser(oldUser, updatedUser))
          case _ => RoomsWS.updateUser(RoomsWS.UpdateUser(oldUser, updatedUser))
        }
        Ok(Json.toJson(updatedUser).transform((__ \ 'password).json.prune).get)
      }
    )
  }

  def edit = StackAction(parse.multipartFormData, AuthorityKey -> NormalUser) { implicit request =>
//    val bucketName = current.configuration.getString("aws.s3.bucket").get
//    val AWS_ACCESS_KEY_ID = current.configuration.getString("aws.key.acccess").get
//    val AwsSecretKey = current.configuration.getString("aws.key.secret").get
//    request.body.file("file").map(_.ref.file) match {
//      case Some(image) =>
//        val result = {
//          val IMAGE_MAX_LENGTH: Long = current.configuration.getLong("user.image.size.max").getOrElse(0L)
//          if (IMAGE_MAX_LENGTH < image.length()) {
//            (false, "小さな画像を選択してください。画像のサイズが大きすぎます")
//          } else {
//            val headerHex = FileUtils.readFileToByteArray(image).take(10).map("%02x" format _).reduce(_ + _)
//            val headerString = new String(FileUtils.readFileToByteArray(image).take(10))
//            def fileType(headerHex: String, headerString: String) =
//              if (headerHex.contains("ffd8")) "JPG"
//              else if (headerString.contains("PNG")) "PNG"
//              else if (headerString.contains("GIF")) "GIF"
//              else if (headerString.contains("BM")) "BMP"
//              else "UNKNOWN"
//            if (fileType(headerHex, headerString) != "UNKNOWN") {
//              val width = ImageIO.read(image).getWidth
//              val height = ImageIO.read(image).getHeight
//              val mode = if (width == height) Scalr.Mode.FIT_TO_WIDTH else if (width < height) Scalr.Mode.FIT_TO_WIDTH else Scalr.Mode.FIT_TO_HEIGHT
//              val bufferedImage = Scalr.resize(ImageIO.read(image), mode, 64, Scalr.OP_ANTIALIAS)
//              val cropOriginX = (bufferedImage.getWidth - 64) / 2
//              val cropOriginY = (bufferedImage.getHeight - 64) / 2
//              val bufferedImage2 = Scalr.crop(bufferedImage, cropOriginX, cropOriginY, 64, 64, Scalr.OP_ANTIALIAS)
//              val outputFile = new File("image.png")
//              ImageIO.write(bufferedImage2, "png", outputFile)
//              def url(key: String) = s"https://s3-ap-northeast-1.amazonaws.com/$bucketName/$key"
//              val result = try {
//                val key = loggedIn.id + "-" + DateTime.now().getMillis
//                val basicAWSCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AwsSecretKey)
//                val clientConfiguration = new ClientConfiguration().withConnectionTimeout(30000)
//                val objectRequest = new PutObjectRequest(bucketName, key, outputFile).withMetadata(new ObjectMetadata().withCacheControl(31536000))
//                new AmazonS3Client(basicAWSCredentials, clientConfiguration).putObject(objectRequest)
//                val newUser = loggedIn.copy(image = Some(url(key)), updatedAt = DateTime.now).save()
//                val result = User.findById(loggedIn.id)
//                Right(result)
//              } catch {
//                case e: AmazonServiceException => Left(e)
//                case e: AmazonClientException => Left(e)
//                case e: Exception => Left(e)
//              }
//              result.fold(
//                left => (false, "画像を再送信してください。画像の登録に失敗しました"),
//                right => (true, "画像の設定に成功しました")
//              )
//            } else {
//              (false, "別の画像を選択してください。JPEG,PNG,GIF,BMP形式の画像のみ有効です")
//            }
//          }
//        }
//        if (result._1) Ok else BadRequest(result._2)
//      case None => BadRequest
//    }
    NotImplemented
  }

  def delete = TODO
}
