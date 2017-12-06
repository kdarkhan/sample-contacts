package controllers

import javax.inject._

import daos.UsersDao
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UsersController @Inject()(
    usersDao: UsersDao,
    cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  def createUserSession = Action.async { implicit request =>
    Future.successful(Ok("create session"))
  }

  def createUser = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateUserForm](CreateUserForm.formatter) match {
      case JsSuccess(formData, _) =>
        usersDao.createUser(formData.username, formData.password) map { user =>
          Created("")
        }
      case JsError(errors) =>
        Future.successful(
          BadRequest(s"""Invalid data ${errors.map(_._2).mkString("\n")}"""))
    }
  }
}

case class CreateUserForm(username: String, password: String)
object CreateUserForm {
  implicit val formatter = Json.format[CreateUserForm]
}
