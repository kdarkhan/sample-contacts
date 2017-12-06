package auth

case class Hashed(hash: String, salt: String)
trait PasswordHasher {
  def hashPassword(password: String): Hashed
  def checkPassword(password: String, hashed: String, salt: String): Boolean
}
