package auth

import scala.concurrent.Future

trait SessionStorage {

  def getUserByToken(token: String): Future[Option[Long]]
  def generateToken(userId: Long): Future[String]
  def deleteToken(token: String): Future[Unit]
}