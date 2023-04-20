package models

import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class QuantityRepository @Inject()(@NamedDatabase("SI") protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

	private val dbConfig = dbConfigProvider.get[JdbcProfile]

	import dbConfig._
	import profile.api._

	private class UnitTable(tag: Tag) extends Table[SIUnit](tag, "units") {

		def symbol: Rep[String] = column[String]("symbol", O.PrimaryKey)

		def name: Rep[String] = column[String]("name")

		def name_fr: Rep[String] = column[String]("name_fr")

		def * : ProvenShape[SIUnit] = (symbol, name, name_fr) <> ((SIUnit.apply _).tupled, SIUnit.unapply)
	}

	private val units = TableQuery[UnitTable]

	def list: Future[Seq[SIUnit]] = db.run {
		units.result
	}
}
