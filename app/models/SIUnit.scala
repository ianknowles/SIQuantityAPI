package models

import models.SIUnit.api_path
import play.api.libs.json._

case class SIUnit(symbol: String, name: String, name_fr: String) {
	val relativeIRI: String = s"/$api_path/$symbol"
}

object SIUnit {
	// TODO get api path from reverse router?
	private val api_path: String = "units"

	implicit val SIUnitFormat: Format[SIUnit] = Json.format[SIUnit]
}
