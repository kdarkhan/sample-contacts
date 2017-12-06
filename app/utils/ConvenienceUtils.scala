package utils

import scala.concurrent.Future

object ConvenienceUtils {

  object Implicits {
    implicit class SuccessFuture[A <: Any](val self: A) extends AnyVal {
      def successful: Future[A] = Future.successful(self)
    }

    implicit class FailedFuture[A <: Throwable](val self: A) extends AnyVal {
      def failure[T]: Future[T] = Future.failed(self)
    }

    implicit class OptionOps[T](val self: T) extends AnyVal {
      def toOption: Option[T] = Option(self)
    }
  }
}
