package com.zxventures.geladinha.components.pointOfSale

import com.vividsolutions.jts.geom.{MultiPolygon, Point}
import com.zxventures.geladinha.infrastructure.persistence.postgres.DBConnection
import com.zxventures.geladinha.infrastructure.persistence.postgres.PostgresDriver.api._

import scala.concurrent.Future

trait PointOfSaleRepository extends DBConnection {

  val pointsOfSale = TableQuery[PointOfSaleModel]

  def add(pointOfSale: PointOfSale): Future[PointOfSale] = {
    val query = pointsOfSale returning pointsOfSale.map(_.id) into ((pos, id) => pos.copy(id = id))
    run(query += pointOfSale)
  }

  def load(id: Long): Future[Option[PointOfSale]] = {
    val query = pointsOfSale.filter(_.id === id).result.headOption
    run(query)
  }

  private[pointOfSale] class PointOfSaleModel(tag: Tag) extends Table[PointOfSale](tag, "points_of_sale") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def tradingName = column[String]("trading_name")
    def ownerName = column[String]("owner_name")
    def document = column[String]("document")
    def coverageArea = column[MultiPolygon]("coverage_area")
    def address = column[Point]("address")

    def * = (
      id,
      tradingName,
      ownerName,
      document,
      coverageArea,
      address
    ) <> (PointOfSale.tupled, PointOfSale.unapply)
  }

}

object PointOfSaleRepository extends PointOfSaleRepository
