package controllers

import models._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class QuantityController @Inject()(repo: QuantityRepository,
                                   cc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)
	extends MessagesAbstractController(cc) with I18nSupport {

	def getUnit(symbol: String): Action[AnyContent] = Action.async { implicit request =>
		repo.findBySymbol(symbol).map {
			case Some(unit) => render {
				case Accepts.Html() => Ok(views.html.unit(List(unit))).withHeaders(CONTENT_LANGUAGE -> "en, fr")
				case Accepts.Json() => Ok(Json.toJson(unit)).withHeaders(CONTENT_LANGUAGE -> "en, fr")
			}
			case None => NotFound
		}
	}

	def getUnitName(symbol: String, lang: Lang)(implicit request: Request[AnyContent]): Future[Result] = {
		repo.findBySymbol(symbol).map {
			case Some(unit) =>
				val (name, code) = lang.code match {
					case "fr" => (unit.name_fr, "fr")
					case _ => (unit.name, "en")
				}
				render {
					case Accepts.Html() => Ok(views.html.main(name)(Html(name))(MessagesImpl(Lang(code), messagesApi))).withHeaders(CONTENT_LANGUAGE -> code)
					case Accepts.Json() => Ok(Json.toJson(name)).withHeaders(CONTENT_LANGUAGE -> code)
				}
			case None => NotFound
		}
	}

	def getUnitLocalisedName(symbol: String, lang: String): Action[AnyContent] = Action.async { implicit request =>
		getUnitName(symbol, Lang(lang))
	}

	def getUnitName(symbol: String): Action[AnyContent] = Action.async { implicit request =>
		getUnitName(symbol, request.lang)
	}

	val getUnits: Action[AnyContent] = Action.async { implicit request =>
		repo.list.map { units =>
			render {
				case Accepts.Html() => Ok(views.html.units(units))
				case Accepts.Json() => Ok(Json.toJson(units))
			}
		}
	}

	def viewUnits: Action[AnyContent] = Action.async { implicit request =>
		repo.list.map { units =>
			Ok(views.html.units(units))
		}
	}

	def asJsonUnits: Action[AnyContent] = Action.async { implicit request =>
		repo.list.map { units =>
			Ok(Json.toJson(units))
		}
	}
}
