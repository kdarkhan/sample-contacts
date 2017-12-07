package mocks

import javax.inject.{Inject, Singleton}

import daos.UsersDao
import models.DbUser
import utils.ConvenienceUtils.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class UsersDaoMockImpl @Inject()()(implicit ec: ExecutionContext)
    extends UsersDao {
  private val storage = collection.mutable.Map.empty[Long, DbUser]
  private var lastId: Long = 0
  def createUser(userName: String,
                 hashedPassword: String,
                 salt: String): Future[DbUser] = {
    if (storage.values.exists(_.username == userName)) {
      new Exception("User already exists").failure
    } else {
      val next = DbUser(
        lastId,
        userName,
        hashedPassword,
        salt
      )
      storage += lastId -> next
      lastId += 1
      next.successful
    }
  }

  def listUsers(): Future[Seq[DbUser]] = {
    storage.values.toSeq.successful
  }

  def findByUsername(username: String): Future[Option[DbUser]] = {
    storage.values.find(_.username == username).successful
  }
}
