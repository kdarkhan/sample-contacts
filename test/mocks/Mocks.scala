package mocks

import daos.{ContactsDao, UsersDao}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

object Mocks {
  def app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[ContactsDao].to[ContactsDaoMockImpl],
      bind[UsersDao].to[UsersDaoMockImpl]
    )
    .build()
}
