package models

import play.api.libs.json._

case class SIUnit(symbol: String, name: String, name_fr: String)

object SIUnit {
  implicit val SIUnitFormat: Format[SIUnit] = Json.format[SIUnit]
}
