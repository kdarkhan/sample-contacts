package auth

import org.mindrot.jbcrypt.BCrypt

class BcryptPasswordHasher extends PasswordHasher {

  def hashPassword(password: String): Hashed = {
    val salt = BCrypt.gensalt()
    Hashed(BCrypt.hashpw(password, salt), salt)
  }

  /**
    * BCrypt stores the hash inside password, so salt is ignored
    * Keeping here in case we decide to replace the hasher
    * @param password
    * @param salt
    * @return
    */
  def checkPassword(password: String, hashed: String, salt: String): Boolean = {
    BCrypt.checkpw(password, hashed)
  }
}
