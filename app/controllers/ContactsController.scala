package controllers

import javax.inject._

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ContactsController @Inject()(
    repo: PersonRepository,
    cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {


  def listAllContacts = Action.async { implicit request =>
    Future.successful(Ok("list contacts"))
  }

  def createContact = Action.async { implicit request =>
    Future.successful(Ok("Create contact"))
  }

  def updateContact(id: Long) = Action.async { implicit request =>
    Future.successful(Ok("Create contact"))
  }

  def deleteContact(id: Long) = Action.async { implicit request =>
    Future.successful(Ok("Create contact"))
  }

  def addPhoneNumber(id: Long) = Action.async { implicit request =>
    Future.successful(Ok("Create contact"))
  }
}
