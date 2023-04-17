package controllers

import com.typesafe.config.ConfigFactory
import models.QuantityRepository
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Configuration
import play.api.i18n.MessagesApi
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
			contentAsString(units) must include ("second")
		}

		"render the units table from the application" in {
			val controller = inject[QuantityController]
			val units = controller.getUnits.apply(FakeRequest(GET, "/units"))

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must include("second")
		}

		"render the units table from the router" in {
			val request = FakeRequest(GET, "/units")
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must include("second")
		}
	}
	"QuantityController /units endpoint" should {

		"render /units/html as HTML" in {
			val request = FakeRequest(GET, "/units/html")
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must include("second")
		}

		"render /units/json as JSON" in {
			val request = FakeRequest(GET, "/units/json")
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			contentAsString(units) must include("second")
		}

		"render /units as JSON when the client accepts only JSON" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			contentAsString(units) must include("second")
		}

		"render /units as JSON when the client prefers JSON" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "application/json, text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("application/json")
			contentAsString(units) must include("second")
		}

		"render /units as HTML when the client accepts only HTML" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must include("second")
		}

		"render /units as HTML when the client prefers HTML" in {
			val request = FakeRequest(GET, "/units").withHeaders(FakeHeaders(Seq(Http.HeaderNames.ACCEPT -> "text/html, application/json", Http.HeaderNames.HOST -> "localhost")))
			val units = route(app, request).get

			status(units) mustBe OK
			contentType(units) mustBe Some("text/html")
			contentAsString(units) must include("second")
		}
	}
}
