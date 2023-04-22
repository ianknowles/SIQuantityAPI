package controllers

import com.typesafe.config.ConfigFactory
import models.QuantityRepository
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsArray, JsObject, JsString}
import play.api.test.Helpers._
import play.api.test._
import play.mvc.Http

import scala.concurrent.ExecutionContext

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class QuantityControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

	"QuantityController GET" should {

		"render the units table from a new instance of controller" in {
			implicit lazy val ec: ExecutionContext = inject[ExecutionContext]
			val controller = new QuantityController(inject[QuantityRepository], stubMessagesControllerComponents())
			val units = controller.getUnits.apply(FakeRequest(GET, "/units"))

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
		}

		"render the units table from the application" in {
			val controller = inject[QuantityController]
			val units = controller.getUnits.apply(FakeRequest(GET, "/units"))

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
		}

		"render the units table from the router" in {
			val request = FakeRequest(GET, "/units")
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
		}
	}
	"QuantityController /units endpoint" should {

		"render /units/html as HTML" in {
			val request = FakeRequest(GET, "/units/html")
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
		}

		"render /units/json as JSON" in {
			val request = FakeRequest(GET, "/units/json")
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
			contentAsJson(units) mustBe a[JsArray]
		}

		"render /units as JSON when the client accepts only JSON" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
			contentAsJson(units) mustBe a[JsArray]
		}

		"render /units as JSON when the client prefers JSON" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json, text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
			contentAsJson(units) mustBe a[JsArray]
		}

		"render /units as HTML when the client accepts only HTML" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
		}

		"render /units as HTML when the client prefers HTML" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html, application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must (include("second") and include("metre") and include("mètre"))
		}
	}
	"QuantityController /units selector" should {

		"/units/S returns NOT FOUND for non-existent unit S" in {
			val request = FakeRequest(GET, "/units/S").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe NOT_FOUND
		}

		"/units/a returns NOT FOUND for non-existent unit a" in {
			val request = FakeRequest(GET, "/units/a").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe NOT_FOUND
		}

		"/units/a returns NOT FOUND for non-existent unit b" in {
			val request = FakeRequest(GET, "/units/b").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe NOT_FOUND
		}

		"render /units/s as JSON when the client accepts only JSON" in {
			val request = FakeRequest(GET, "/units/s").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			header(CONTENT_LANGUAGE, units) mustBe Some("en, fr")
			contentAsString(units) must (include("second") and include("seconde") and not include "metre" and not include "mètre")
			contentAsJson(units) mustBe a[JsObject]
		}

		"render /units/s as HTML when the client accepts only HTML" in {
			val request = FakeRequest(GET, "/units/s").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			header(CONTENT_LANGUAGE, units) mustBe Some("en, fr")
			contentAsString(units) must (include("<html lang=\"en\">") and include("second") and include("seconde") and not include("metre") and not include("mètre"))
		}

		"render /units/m as JSON when the client accepts only JSON" in {
			val request = FakeRequest(GET, "/units/m").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			header(CONTENT_LANGUAGE, units) mustBe Some("en, fr")
			contentAsString(units) must (not include("second") and not include("seconde") and include("metre") and include("mètre"))
			contentAsJson(units) mustBe a[JsObject]
		}

		"render /units/m as HTML when the client accepts only HTML" in {
			val request = FakeRequest(GET, "/units/m").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			header(CONTENT_LANGUAGE, units) mustBe Some("en, fr")
			contentAsString(units) must (include("<html lang=\"en\">") and not include("second") and not include("seconde") and include("metre") and include("mètre"))
		}

		"render /units/m/name as JSON when the client accepts only JSON" in {
			val request = FakeRequest(GET, "/units/m/name").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			header(CONTENT_LANGUAGE, units) mustBe Some("en")
			contentAsString(units) must (not include("second") and not include("seconde") and include("metre") and not include("mètre"))
			contentAsJson(units) mustBe a[JsString]
		}

		"render /units/m/name as HTML when the client accepts only HTML" in {
			val request = FakeRequest(GET, "/units/m/name").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			header(CONTENT_LANGUAGE, units) mustBe Some("en")
			contentAsString(units) must (include("<html lang=\"en\">") and not include("second") and not include("seconde") and include("metre") and not include("mètre"))
		}

		"render /units/m/name in French when requested, as JSON when the client accepts only JSON" in {
			val request = FakeRequest(GET, "/units/m/name").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost", ACCEPT_LANGUAGE -> "fr")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			header(CONTENT_LANGUAGE, units) mustBe Some("fr")
			contentAsString(units) must (not include("second") and not include("seconde") and not include("metre") and include("mètre"))
			contentAsJson(units) mustBe a[JsString]
		}

		"render /units/m/name in French when requested, as HTML when the client accepts only HTML" in {
			val request = FakeRequest(GET, "/units/m/name").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html", Http.HeaderNames.HOST -> "localhost", ACCEPT_LANGUAGE -> "fr")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			header(CONTENT_LANGUAGE, units) mustBe Some("fr")
			contentAsString(units) must (include("<html lang=\"fr\">") and not include("second") and not include("seconde") and not include("metre") and include("mètre"))
		}

		"render /unités/m/nom in French, as JSON when the client accepts only JSON" in {
			val request = FakeRequest(GET, "/unit%C3%A9s/m/nom").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			header(CONTENT_LANGUAGE, units) mustBe Some("fr")
			contentAsString(units) must (not include("second") and not include("seconde") and not include("metre") and include("mètre"))
			contentAsJson(units) mustBe a[JsString]
		}

		"render /unités/m/nom in French, as HTML when the client accepts only HTML" in {
			val request = FakeRequest(GET, "/unit%C3%A9s/m/nom").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			header(CONTENT_LANGUAGE, units) mustBe Some("fr")
			contentAsString(units) must (include("<html lang=\"fr\">") and not include("second") and not include("seconde") and not include("metre") and include("mètre"))
		}
	}
}
