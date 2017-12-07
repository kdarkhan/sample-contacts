import java.util.concurrent.TimeUnit

import controllers.CreateContactForm._
import controllers.{AddPhoneNumberForm, CreateContactForm, CreateUserForm}
import mocks.Mocks
import models.Contact
import org.junit.runner._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable._
import org.specs2.runner._
import play.api.Application
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
@RunWith(classOf[JUnitRunner])
class ContactsSpec(implicit ee: ExecutionEnv) extends Specification {
  "ContactsController" should {

    val duration = FiniteDuration(10, TimeUnit.SECONDS)
    def createUserAndAuth(
        username: String = "username",
        password: String = "password")(implicit app: Application): String = {
      Await.result(route(app,
                         FakeRequest(POST, "/user").withJsonBody(
                           Json.toJsObject(CreateUserForm(username, password))
                         )).get,
                   duration)

      val parsed = contentAsJson(
        route(app,
              FakeRequest(POST, "/user/sessions").withJsonBody(
                Json.toJsObject(CreateUserForm(username, password)))).get)
      parsed
        .asInstanceOf[JsObject]
        .fields
        .find(_._1 == "token")
        .get
        ._2
        .asInstanceOf[JsString]
        .value
    }

    "list empty user contacts" in new WithApplication(Mocks.app) {
      val token = createUserAndAuth()
      route(app,
            FakeRequest(GET, "/contacts")
              .withHeaders("token" -> token)) must beSome.which(resp => {
        status(resp) must_== OK
        contentAsJson(resp) must_== JsArray(Seq.empty)
      })
    }

    "forbid unauthorized access" in new WithApplication(Mocks.app) {
      route(app,
            FakeRequest(GET, "/contacts")
              .withHeaders("token" -> "invalid token")) must beSome.which(
        status(_) must_== UNAUTHORIZED
      )
    }

    "allow creating contacts" in new WithApplication(Mocks.app) {
      val token = createUserAndAuth()
      route(
        app,
        FakeRequest(POST, "/contacts")
          .withHeaders("token" -> token)
          .withJsonBody(
            Json.toJsObject(
              CreateContactForm(
                "contact_first_name",
                "contact_last_name"
              ))
          )
      ) must beSome.which(
        status(_) must_== CREATED
      )
      route(app,
            FakeRequest(GET, "/contacts")
              .withHeaders("token" -> token)) must beSome.which(resp => {
        status(resp) must_== OK
        val parsed = contentAsJson(resp)
        parsed must haveClass[JsArray]
        parsed.asInstanceOf[JsArray].value.length must_== 1
      })

    }

    "allow updating contacts" in new WithApplication(Mocks.app) {
      val token = createUserAndAuth()
      val contactId = contentAsJson(
        route(
          app,
          FakeRequest(POST, "/contacts")
            .withHeaders("token" -> token)
            .withJsonBody(
              Json.toJsObject(
                CreateContactForm(
                  "contact_first_name",
                  "contact_last_name"
                ))
            )
        ).get)
        .asInstanceOf[JsObject]
        .fields
        .find(_._1 == "id")
        .get
        ._2
        .asInstanceOf[JsNumber]
        .value
        .toLong

      route(
        app,
        FakeRequest(POST, s"/contacts/$contactId")
          .withHeaders("token" -> token)
          .withJsonBody(
            Json.toJsObject(
              CreateContactForm(
                "updated_first_name",
                "updated_last_name"
              ))
          )
      ) must beSome.which {
        status(_) must_== OK
      }

      route(
        app,
        FakeRequest(POST, s"/contacts/$contactId/entries")
          .withHeaders("token" -> token)
          .withJsonBody(
            Json.toJsObject(
              AddPhoneNumberForm(
                "123123123"
              ))
          )
      ) must beSome.which {
        status(_) must_== CREATED
      }

      val Some(resp) = route(app,
                             FakeRequest(GET, "/contacts")
                               .withHeaders("token" -> token))
      val parsed = contentAsJson(resp)
      parsed must haveClass[JsArray]
      val array = parsed.asInstanceOf[JsArray].value
      array.length must_== 1
      array.collect {
        case o: JsObject =>
          o.validate[Contact].asOpt.map { c =>
            c.lastName must_== "updated_last_name"
            c.firstName must_== "updated_first_name"
            c.phones must_== List("123123123")
          }
      }
    }

    "allow deleting contacts" in new WithApplication(Mocks.app) {
      val token = createUserAndAuth()
      val contactId = contentAsJson(
        route(
          app,
          FakeRequest(POST, "/contacts")
            .withHeaders("token" -> token)
            .withJsonBody(
              Json.toJsObject(
                CreateContactForm(
                  "contact_first_name",
                  "contact_last_name"
                ))
            )
        ).get)
        .asInstanceOf[JsObject]
        .fields
        .find(_._1 == "id")
        .get
        ._2
        .asInstanceOf[JsNumber]
        .value
        .toLong

      val Some(resp) = route(app,
                             FakeRequest(GET, "/contacts")
                               .withHeaders("token" -> token))
      val parsed = contentAsJson(resp)
      parsed must haveClass[JsArray]
      val array = parsed.asInstanceOf[JsArray].value
      array.length must_== 1

      contentAsString(
        route(app,
              FakeRequest(DELETE, s"/contacts/$contactId")
                .withHeaders("token" -> token)).get) must_== ""

      val Some(afterDelete) = route(app,
                                    FakeRequest(GET, "/contacts")
                                      .withHeaders("token" -> token))
      val parsedAfterDelete = contentAsJson(afterDelete)
      parsedAfterDelete must haveClass[JsArray]
      val arrayAfterDelete = parsedAfterDelete.asInstanceOf[JsArray].value
      arrayAfterDelete.isEmpty must_== true

    }
  }
}
