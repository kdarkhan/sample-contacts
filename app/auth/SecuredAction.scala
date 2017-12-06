package auth

import com.google.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import utils.ConvenienceUtils.Implicits._

class AuthenticatedRequest[A](val userId: Long, request: Request[A])
    extends WrappedRequest[A](request)

class SecuredAction @Inject()(val parser: BodyParsers.Default,
                              sessionStorage: SessionStorage)(
    implicit val executionContext: ExecutionContext)
    extends ActionBuilder[AuthenticatedRequest, AnyContent]
    with ActionRefiner[Request, AuthenticatedRequest]
    with Results {

  protected def refine[A](
      request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = {
    request.headers.get("token") match {
      case Some(token) =>
        sessionStorage.getUserByToken(token) map {
          case Some(userId) =>
            Right(new AuthenticatedRequest[A](userId, request))
          case None => Left(Unauthorized)
        }
      case None =>
        Left(Unauthorized).successful
    }
  }
}
