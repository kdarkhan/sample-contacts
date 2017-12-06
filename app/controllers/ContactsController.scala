package controllers

import javax.inject._

import daos.{ContactsDao, UsersDao}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ContactsController @Inject(
  )(usersDao: UsersDao,
    contactsDao: ContactsDao,
    cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  val contactForm: Form[CreateContactForm] = Form {
    mapping(
      "first_name" -> nonEmptyText,
      "last_name" -> nonEmptyText
    )(CreateContactForm.apply)(CreateContactForm.unapply)
  }

  val phoneForm: Form[AddPhoneNumberForm] = Form {
    mapping(
      "phone" -> nonEmptyText
    )(AddPhoneNumberForm.apply)(AddPhoneNumberForm.unapply)
  }

  def listAllContacts = Action.async { implicit request =>
    // TODO get actual userId
    val userId = 100L
    contactsDao.listUserContacts(userId) map { c =>
      Ok(Json.toJson(c))
    }
  }

  def createContact = Action.async { implicit request =>
    val userId = 100L // TODO
    contactForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(errorForm.errors.toString))
      },
      formData => {
        contactsDao
          .createContact(userId, formData.firstName, formData.lastName)
          .map { c =>
            Ok(
              Json.obj(
                "id" -> c.id
              )
            )
          }
      }
    )
  }

  def updateContact(id: Long) = Action.async { implicit request =>
    // TODO check permissions
    contactForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(errorForm.errors.toString))
      },
      formData => {
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
      }
    )
  }

  def deleteContact(id: Long) = Action.async { implicit request =>
    // TODO: check permissions
    contactsDao.deleteContact(id) map {
      case true  => NoContent
      case false => BadRequest("Contact does not exist")
    }
  }

  def addPhoneNumber(id: Long) = Action.async { implicit request =>
    val userId = 100L
    phoneForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(errorForm.errors.toString))
      },
      formData => {
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
      }
    )
  }
}

case class CreateContactForm(firstName: String, lastName: String)
case class AddPhoneNumberForm(phone: String)
