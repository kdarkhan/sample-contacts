package modules

import auth._
import com.google.inject.AbstractModule
import daos.{ContactsDao, ContactsDaoImpl, UsersDao, UsersDaoImpl}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UsersDao]).to(classOf[UsersDaoImpl])
    bind(classOf[ContactsDao]).to(classOf[ContactsDaoImpl])
    bind(classOf[SessionStorage]).to(classOf[SessionStorageImpl])
    bind(classOf[SecuredAction]).asEagerSingleton()
    bind(classOf[PasswordHasher]).to(classOf[BcryptPasswordHasher])
  }
}
