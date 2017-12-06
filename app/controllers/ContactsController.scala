package controllers

import javax.inject._

import daos.{ContactsDao, UsersDao}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ContactsController @Inject(
  )(usersDao: UsersDao,
    contactsDao: ContactsDao,
    cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  def listAllContacts = Action.async { implicit request =>
    // TODO get actual userId
    val userId = 100L
    contactsDao.listUserContacts(userId) map { c =>
      Ok(Json.toJson(c))
    }
  }

  def createContact = Action.async(parse.json) { implicit request =>
    val userId = 100L // TODO
    request.body
      .validate[CreateContactForm](CreateContactForm.formatter) match {
      case JsSuccess(formData, _) =>
        contactsDao
          .createContact(userId, formData.firstName, formData.lastName)
          .map { c =>
            Ok(
              Json.obj(
                "id" -> c.id
              )
            )
          }
      case JsError(errors) =>
        Future.successful(BadRequest(errors.toString))
    }
  }

  def updateContact(id: Long) = Action.async(parse.json) { implicit request =>
    // TODO check permissions
    request.body
      .validate[CreateContactForm](CreateContactForm.formatter) match {
      case JsSuccess(formData, _) =>
        contactsDao
          .updateContact(id, formData.firstName, formData.lastName)
          .map {
            case true =>
              Ok(
                Json.obj(
                  "id" -> id
                )
              )
            case false => BadRequest("Contact is not found")
          }
      case JsError(errors) =>
        Future.successful(BadRequest(errors.toString))
    }
  }

  def deleteContact(id: Long) = Action.async { implicit request =>
    // TODO: check permissions
    contactsDao.deleteContact(id) map {
      case true  => NoContent
      case false => BadRequest("Contact does not exist")
    }
  }

  def addPhoneNumber(id: Long) = Action.async(parse.json) { implicit request =>
    val userId = 100L
    request.body
      .validate[AddPhoneNumberForm](AddPhoneNumberForm.formatter) match {
      case JsSuccess(formData, _) =>
        contactsDao.getById(id).flatMap {
          case None => Future.successful(BadRequest("Contact is not found"))
          case Some(c) =>
            if (c.owner == userId) {
              contactsDao
                .updatePhones(id, c.phones :+ formData.phone)
                .map {
                  case true =>
                    Ok(
                      Json.obj(
                        "id" -> id
                      )
                    )
                  case false => BadRequest("Contact is not found")
                }
            } else {
              Future.successful(Unauthorized("Cannot edit contact"))
            }
        }
      case JsError(errors) =>
        Future.successful(BadRequest(errors.toString))
    }
  }
}

case class CreateContactForm(firstName: String, lastName: String)
object CreateContactForm {
  implicit val formatter = Json.format[CreateContactForm]
}

case class AddPhoneNumberForm(phone: String)
object AddPhoneNumberForm {
  implicit val formatter = Json.format[AddPhoneNumberForm]
}
