package daos

import javax.inject.{Inject, Singleton}

import models.{Contact, Person}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * A repository for people.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class ContactsDaoImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends ContactsDao {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[MyPostgresProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of people
    */
  private class ContactsTable(tag: Tag)
      extends Table[Contact](tag, "contacts") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def phones = column[List[String]]("phones")
    def owner = column[Long]("owner")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Person object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Person case classes
      * apply and unapply methods.
      */
    def * =
      (id, firstName, lastName, phones, owner) <> ((Contact.apply _).tupled, Contact.unapply)
  }

  /**
    * The starting point for all queries on the people table.
    */
  private val contacts = TableQuery[ContactsTable]

  def listUserContacts(userId: Long): Future[Seq[Contact]] = db.run {
    contacts.filter(_.owner === userId).result
  }

  def findById(id: Long): Future[Option[Contact]] = db.run {
    contacts.filter(_.id === id).result.headOption
  }

  def createContact(ownerId: Long,
                    firstName: String,
                    lastName: String): Future[Contact] = db.run {
    (contacts.map(p => (p.firstName, p.lastName, p.phones, p.owner))
      returning contacts.map(_.id)
      into ((data, id) => Contact(id, data._1, data._2, data._3, data._4))) += (firstName, lastName, List.empty, ownerId)
  }

  def updateContact(contactId: Long,
                    firstName: String,
                    lastName: String): Future[Boolean] = {
    db.run {
      val q = for {
        c <- contacts if c.id === contactId
      } yield (c.firstName, c.lastName)
      q.update((firstName, lastName))
    } map {
      _ > 0
    }
  }

   def updatePhones(contactId: Long,
                    phones: List[String]): Future[Boolean] = {
    db.run {
      val q = for {
        c <- contacts if c.id === contactId
      } yield c.phones
      q.update(phones)
    } map {
      _ > 0
    }
  }

  def deleteContact(contactId: Long): Future[Boolean] = {
    db.run {
      contacts.filter(_.id === contactId).delete
    } map { _ > 0 }
  }
}
