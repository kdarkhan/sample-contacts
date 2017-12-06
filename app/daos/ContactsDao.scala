package daos

import models.Contact

import scala.concurrent.Future

trait ContactsDao {
  def getById(id: Long): Future[Option[Contact]]
  def listUserContacts(userId: Long): Future[Seq[Contact]]
  def createContact(ownerId: Long, firstName: String, lastName: String): Future[Contact]
  def updateContact(contactId: Long, firstName: String, lastName: String): Future[Boolean]
  def updatePhones(contactId: Long, phones: List[String]): Future[Boolean]
  def deleteContact(contactId: Long): Future[Boolean]
}