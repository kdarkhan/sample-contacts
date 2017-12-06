package controllers

import javax.inject._

import daos.UsersDao
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UsersController @Inject()(
    usersDao: UsersDao,
    cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  /**
    * The mapping for the person form.
    */
  val createUserForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> text.verifying("Password length should be greater than 5",
                              _.length > 5)
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  def createUserSession = Action.async { implicit request =>
    Future.successful(Ok("create session"))
  }

  def createUser = Action.async { implicit request =>
    Future.successful(Ok("create user"))
    createUserForm.bindFromRequest.fold(
      errors => {
        Future.successful(
          BadRequest(
            s"""Invalid data ${errors.errors.map(_.message).mkString("\n")}"""))
      },
      formData => {
        usersDao.createUser(formData.username, formData.password) map { user =>
          Created("")
        }
      }
    )
  }
}

case class CreateUserForm(username: String, password: String)
