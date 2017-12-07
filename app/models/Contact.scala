package models

import play.api.libs.json.{JsPath, Json, Writes}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

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

  implicit val reader = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "first_name").read[String] and
      (JsPath \ "last_name").read[String] and
      (JsPath \ "phones").read[List[String]] and
      (JsPath \ "owner").readWithDefault[Long](0) // json reader is used only in tests
  )(Contact.apply _)
}
