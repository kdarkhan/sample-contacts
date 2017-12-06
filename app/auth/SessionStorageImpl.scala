package auth

import java.util.concurrent.TimeUnit
import javax.inject.Inject

import play.api.cache.AsyncCacheApi

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class SessionStorageImpl @Inject()(cacheApi: AsyncCacheApi)
    extends SessionStorage {
  private val r = new scala.util.Random(31)
  val TokenLength = 10
  val TokenDuration = Duration(30, TimeUnit.MINUTES)

  def getUserByToken(token: String): Future[Option[Long]] = {
    cacheApi.get[Long](token)
  }

  def generateToken(userId: Long): Future[String] = {
    val token = r.nextString(TokenLength)
    cacheApi.set(token, userId, TokenDuration).map(_ => token)
  }

  def deleteToken(token: String): Future[Unit] = {
    cacheApi.remove(token).map(_ => ())
  }
}
