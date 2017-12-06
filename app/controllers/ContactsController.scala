package controllers

import javax.inject._

import auth.SecuredAction
import daos.{ContactsDao, UsersDao}
import play.api.libs.json._
import play.api.mvc._
import utils.ConvenienceUtils.Implicits._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext

class ContactsController @Inject(
  )(usersDao: UsersDao,
    contactsDao: ContactsDao,
    securedAction: SecuredAction,
    cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  def listAllContacts = securedAction.async { implicit request =>
    contactsDao.listUserContacts(request.userId) map { c =>
      Ok(Json.toJson(c))
    }
  }

  def createContact = securedAction.async(parse.json) { implicit request =>
    request.body
      .validate[CreateContactForm](CreateContactForm.reader) match {
      case JsSuccess(formData, _) =>
        contactsDao
          .createContact(request.userId, formData.firstName, formData.lastName)
          .map { c =>
            Ok(Json.obj("id" -> c.id))
          }
      case JsError(errors) =>
        BadRequest(errors.toString).successful
    }
  }

  def updateContact(id: Long) = securedAction.async(parse.json) {
    implicit request =>
      contactsDao.findById(id) flatMap {
        case Some(contact) =>
          if (contact.owner == request.userId) {
            request.body
              .validate[CreateContactForm](CreateContactForm.reader) match {
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
                BadRequest(errors.toString).successful
            }
          } else {
            Forbidden("Cannot update this contact").successful
          }
        case None => NotFound("Contact does not exist").successful
      }
  }

  def deleteContact(id: Long) = securedAction.async { implicit request =>
    contactsDao.findById(id) flatMap {
      case Some(contact) =>
        if (contact.owner == request.userId) {
          contactsDao.deleteContact(id).map(_ => NoContent)
        } else Forbidden.successful
      case None =>
        NotFound.successful
    }
  }

  def addPhoneNumber(id: Long) = securedAction.async(parse.json) {
    implicit request =>
      request.body
        .validate[AddPhoneNumberForm](AddPhoneNumberForm.reader) match {
        case JsSuccess(formData, _) =>
          contactsDao.findById(id).flatMap {
            case None => NotFound("Contact is not found").successful
            case Some(c) =>
              if (c.owner == request.userId) {
                contactsDao
                  .updatePhones(id, c.phones :+ formData.phone)
                  .map {
                    case true  => Created(Json.obj("id" -> id))
                    case false => BadRequest("Contact is not found")
                  }
              } else {
                Forbidden("Cannot edit contact").successful
              }
          }
        case JsError(errors) =>
          BadRequest(errors.toString).successful
      }
  }
}

case class CreateContactForm(firstName: String, lastName: String)
object CreateContactForm {
  implicit val reader: Reads[CreateContactForm] = (
    (JsPath \ "first_name").read[String] and
      (JsPath \ "last_name").read[String]
  )(CreateContactForm.apply _).filter(form =>
    form.firstName.nonEmpty || form.lastName.nonEmpty)
}

case class AddPhoneNumberForm(phone: String)
object AddPhoneNumberForm {
  implicit val reader = Json.format[AddPhoneNumberForm].filter(_.phone.nonEmpty)
}
