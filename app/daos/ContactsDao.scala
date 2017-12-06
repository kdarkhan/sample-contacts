package dao

import models.Contact

import scala.concurrent.Future

trait ContactsDao {
  def listUserContacts(userId: Long): Future[Seq[Contact]]
  def createContact(ownerId: Long, firstName: String, lastName: String): Future[Contact]
  def updateContact(contactId: Long, firstName: String, lastName: String): Future[Boolean]
  def deleteContact(ownerId: Long, contactId: Long): Future[Boolean]
}