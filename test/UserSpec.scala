import controllers.CreateUserForm
import mocks.Mocks
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class UserSpec extends Specification {
  "UserController" should {

    "create new user" in new WithApplication(Mocks.app) {
      route(app,
            FakeRequest(POST, "/user").withJsonBody(
              Json.toJsObject(CreateUserForm("username", "password"))
            )) must beSome.which(status(_) == CREATED)
    }

    "not allow creating users with the same username" in new WithApplication(
      Mocks.app) {
      route(app,
            FakeRequest(POST, "/user").withJsonBody(
              Json.toJsObject(CreateUserForm("username", "password"))
            )) must beSome.which(status(_) == CREATED)
      route(app,
            FakeRequest(POST, "/user").withJsonBody(
              Json.toJsObject(CreateUserForm("username", "password"))
            )) must beSome.which(status(_) == BAD_REQUEST)
    }

    "create valid session for existing user" in new WithApplication(Mocks.app) {
      route(app,
            FakeRequest(POST, "/user").withJsonBody(
              Json.toJsObject(CreateUserForm("username", "password"))
            )) must beSome.which(status(_) == CREATED)
      val sessionResult =
        route(app,
              FakeRequest(POST, "/user/sessions").withJsonBody(
                Json.toJsObject(CreateUserForm("username", "password"))
              ))
      sessionResult must beSome.which { resp =>
        val parsed = contentAsJson(resp)
        parsed must haveClass[JsObject]
        parsed.asInstanceOf[JsObject].keys.contains("token") must_== true
      }
    }

    "not create session with wrong password" in new WithApplication(Mocks.app) {
      route(app,
            FakeRequest(POST, "/user").withJsonBody(
              Json.toJsObject(CreateUserForm("username", "password"))
            )) must beSome.which(status(_) == CREATED)
      val sessionResult =
        route(app,
              FakeRequest(POST, "/user/sessions").withJsonBody(
                Json.toJsObject(CreateUserForm("username", "wrong_password"))
              ))
      sessionResult must beSome.which { resp =>
        status(resp) must_== FORBIDDEN
      }
    }

    "not create session for non existing user" in new WithApplication(Mocks.app) {
      route(app,
            FakeRequest(POST, "/user/sessions").withJsonBody(
              Json.toJsObject(CreateUserForm("non-existing", "wrong_password"))
            )) must beSome.which { resp =>
        status(resp) must_== UNAUTHORIZED
      }
    }
  }
}
