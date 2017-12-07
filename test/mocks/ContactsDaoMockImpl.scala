package mocks

import javax.inject.Inject

import daos.ContactsDao
import models.Contact
import utils.ConvenienceUtils.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class ContactsDaoMockImpl @Inject()()(implicit ec: ExecutionContext)
    extends ContactsDao {
  private val storage = collection.mutable.Map.empty[Long, Contact]
  private var lastId: Long = 0

  def findById(id: Long): Future[Option[Contact]] = storage.get(id).successful

  def listUserContacts(userId: Long): Future[Seq[Contact]] =
    storage.values.filter(_.owner == userId).toSeq.successful

  def createContact(ownerId: Long,
                    firstName: String,
                    lastName: String): Future[Contact] = {
    val next = Contact(
      lastId,
      firstName,
      lastName,
      Nil,
      ownerId
    )
    storage += lastId -> next
    lastId += 1
    next.successful
  }

  def updateContact(contactId: Long,
                    firstName: String,
                    lastName: String): Future[Boolean] = {
    if (storage.contains(contactId)) {
      val c = storage(contactId)
      storage += (contactId -> c.copy(
        firstName = firstName,
        lastName = lastName
      ))
      true.successful
    } else {
      false.successful
    }
  }

  def updatePhones(contactId: Long, phones: List[String]): Future[Boolean] = {
    if (storage.contains(contactId)) {
      val c = storage(contactId)
      storage += (contactId -> c.copy(
        phones = phones
      ))
      true.successful
    } else {
      false.successful
    }
  }

  def deleteContact(contactId: Long): Future[Boolean] = {
    if (storage.contains(contactId)) {
      storage -= contactId
      true.successful
    } else {
      false.successful
    }
  }
}
