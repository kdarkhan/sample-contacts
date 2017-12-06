package models

import play.api.libs.json.{Json, Writes}

case class Contact(id: Long,
                   firstName: String,
                   lastName: String,
                   phones: List[String],
                   owner: Long)

object Contact {
  implicit val contactFormat: Writes[Contact] = (o: Contact) =>
    Json.obj(
      "id" -> o.id,
      "first_name" -> o.firstName,
      "last_name" -> o.lastName,
      "phones" -> Json.toJson(o.phones)
  )
}
