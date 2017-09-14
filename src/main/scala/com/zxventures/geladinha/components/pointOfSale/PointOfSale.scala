package com.zxventures.geladinha.components.pointOfSale

import java.util.UUID

import com.vividsolutions.jts.geom.{MultiPolygon, Point}

case class PointOfSale(id: Long = 0, tradingName: String, ownerName: String, document: String, coverageArea: MultiPolygon, address: Point)

case class PointOfSaleCreate(tradingName: Option[String], ownerName: Option[String], document: Option[String], coverageArea: Option[MultiPolygon], address: Option[Point])
