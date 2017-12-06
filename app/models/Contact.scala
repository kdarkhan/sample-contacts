package models

import play.api.libs.json.{Json, OFormat}

case class Contact(id: Long,
                   firstName: String,
                   lastName: String,
                   phones: List[String],
                   owner: Long)

object Contact {
  implicit val contactFormat: OFormat[Contact] = Json.format[Contact]
}
