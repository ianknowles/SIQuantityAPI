package controllers

import javax.inject._

import models._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class QuantityController @Inject()(repo: QuantityRepository,
                                 cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) with I18nSupport {

  val getUnits: Action[AnyContent] = Action.async { implicit request =>
    repo.list().map { units =>
      render {
        case Accepts.Html() => Ok(views.html.units(units))
        case Accepts.Json() => Ok(Json.toJson(units))
      }
    }
  }

  def viewUnits: Action[AnyContent] = Action.async { implicit request =>
    repo.list().map { units =>
      Ok(views.html.units(units))
    }
  }

  def asJsonUnits: Action[AnyContent] = Action.async { implicit request =>
    repo.list().map { units =>
      Ok(Json.toJson(units))
    }
  }
}
