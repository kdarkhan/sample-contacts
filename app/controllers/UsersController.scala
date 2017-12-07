package controllers

import javax.inject._

import auth.{PasswordHasher, SecuredAction, SessionStorage}
import daos.UsersDao
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import utils.ConvenienceUtils.Implicits._
import utils.LoggingAction

import scala.concurrent.ExecutionContext

class UsersController @Inject()(
    usersDao: UsersDao,
    cc: MessagesControllerComponents,
    securedAction: SecuredAction,
    loggingAction: LoggingAction,
    passwordHasher: PasswordHasher,
    sessionStorage: SessionStorage)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  def index = loggingAction { implicit request =>
    Ok("The app has started. It provides /contacts and /user api")
  }

  def createUserSession = loggingAction.async(parse.json) { implicit request =>
    request.body.validate[CreateUserForm](CreateUserForm.formatter) match {
      case JsSuccess(formData, _) =>
        usersDao.findByUsername(formData.username) flatMap {
          case Some(user) =>
            if (passwordHasher.checkPassword(formData.password,
                                             user.hashedPassword,
                                             user.salt)) {
              sessionStorage.generateToken(user.id).map { token =>
                Ok(Json.obj("token" -> token))
              }
            } else {
              Forbidden("Bad password").successful
            }
          case None => Unauthorized.successful
        }
      case JsError(errors) =>
        BadRequest(s"""Invalid data ${errors.map(_._2).mkString("\n")}""").successful
    }
  }

  def createUser = loggingAction.async(parse.json) { implicit request =>
    request.body.validate[CreateUserForm](CreateUserForm.formatter) match {
      case JsSuccess(formData, _) =>
        val hashed = passwordHasher.hashPassword(formData.password)
        usersDao.findByUsername(formData.username) flatMap {
          case Some(_) => BadRequest("User already exists").successful
          case None =>
            usersDao
              .createUser(formData.username, hashed.hash, hashed.salt) map {
              _ =>
                Created(Json.obj())
            }
        }

      case JsError(errors) =>
        BadRequest(s"""Invalid data ${errors.map(_._2).mkString("\n")}""").successful
    }
  }
}

case class CreateUserForm(username: String, password: String)
object CreateUserForm {
  implicit val formatter = Json.format[CreateUserForm]
}
