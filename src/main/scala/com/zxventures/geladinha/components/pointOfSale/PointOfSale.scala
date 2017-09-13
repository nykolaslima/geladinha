package com.zxventures.geladinha.components.pointOfSale

import com.vividsolutions.jts.geom.{MultiPolygon, Point}

case class PointOfSale(id: Option[Long], tradingName: String, ownerName: String, document: String, coverageArea: MultiPolygon, address: Point)

case class PointOfSaleCreate(tradingName: Option[String], ownerName: Option[String], document: Option[String], coverageArea: Option[MultiPolygon], address: Option[Point])
