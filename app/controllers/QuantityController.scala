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

	private def renderUnitsAsHtml(units: Seq[SIUnit])(implicit request: Request[AnyContent]): Result = {
		Ok(views.html.units(units)).withHeaders(CONTENT_LANGUAGE -> "en, fr")
	}

	private def renderUnitsAsJson(units: Seq[SIUnit]): Result = {
		Ok(Json.toJson(units)).withHeaders(CONTENT_LANGUAGE -> "en, fr")
	}

	def getUnits: Action[AnyContent] = Action.async { implicit request =>
		repo.list.map { units =>
			render {
				case Accepts.Html() => renderUnitsAsHtml(units)
				case Accepts.Json() => renderUnitsAsJson(units)
			}
		}
	}

	def viewUnits: Action[AnyContent] = Action.async { implicit request =>
		repo.list.map(renderUnitsAsHtml)
	}

	def getUnitsAsJson: Action[AnyContent] = Action.async {
		repo.list.map(renderUnitsAsJson)
	}
}
