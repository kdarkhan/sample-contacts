package utils

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class Logging[A](action: Action[A]) extends Action[A] {


  def apply(request: Request[A]): Future[Result] = {
    val result = action(request)
    result.foreach(result => {
      val msg
        : String = s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}" +
        s" status=${result.header.status}"
      LoggingUtils.accessLogger.info(msg)
    })(action.executionContext)
    result
  }

  override def parser = action.parser
  override def executionContext = action.executionContext
}

class LoggingAction @Inject()(parser: BodyParsers.Default)(
    implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
    block(request)
  }
  override def composeAction[A](action: Action[A]) = new Logging(action)
}

object LoggingUtils {
  val accessLogger = Logger("access")
}
