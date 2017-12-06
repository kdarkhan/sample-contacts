package daos

import models.DbUser

import scala.concurrent.Future

trait UsersDao {
  def createUser(userName: String, password: String): Future[DbUser]
  def listUsers(): Future[Seq[DbUser]]
}