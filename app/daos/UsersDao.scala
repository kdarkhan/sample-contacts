package daos

import models.DbUser

import scala.concurrent.Future

trait UsersDao {
  def createUser(userName: String, hashedPassword: String, salt: String): Future[DbUser]
  def listUsers(): Future[Seq[DbUser]]
  def findByUsername(username: String): Future[Option[DbUser]]
}
