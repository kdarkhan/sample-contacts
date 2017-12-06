package models

import play.api.libs.json.{Json, OFormat}

case class DbUser(id: Long,
                  username: String,
                  hashedPassword: String,
                  salt: String)

object DbUser {
  implicit val userFormat: OFormat[DbUser] = Json.format[DbUser]
}
