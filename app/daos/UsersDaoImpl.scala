package daos

import javax.inject.{Inject, Singleton}

import models.DbUser
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * A repository for people.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class UsersDaoImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends UsersDao {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[MyPostgresProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of people
    */
  private class UsersTable(tag: Tag) extends Table[DbUser](tag, "users") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def hashedPassword = column[String]("hashed_password")
    def salt = column[String]("salt")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Person object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Person case classes
      * apply and unapply methods.
      */
    def * =
      (id, username, hashedPassword, salt) <> ((DbUser.apply _).tupled, DbUser.unapply)
  }

  /**
    * The starting point for all queries on the people table.
    */
  private val users = TableQuery[UsersTable]

  def listUsers(): Future[Seq[DbUser]] = db.run {
    users.result
  }

  def createUser(username: String, password: String): Future[DbUser] =
    db.run {
      (users.map(p => (p.username, p.hashedPassword, p.salt))
        returning users.map(_.id)
        into ((data, id) => DbUser(id, data._1, data._2, data._3))) += (username, password, password)
    }
}
