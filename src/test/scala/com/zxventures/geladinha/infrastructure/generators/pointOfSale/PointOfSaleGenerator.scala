package com.zxventures.geladinha.infrastructure.generators.pointOfSale

import com.vividsolutions.jts.geom._
import com.zxventures.geladinha.components.pointOfSale.{PointOfSale, PointOfSaleCreate}
import com.zxventures.geladinha.resources.pointOfSale.{PointOfSale => PointOfSaleResource}
import com.zxventures.geladinha.resources.geometry.{MultiPolygon => MultiPolygonResource, Polygon => PolygonResource, LinearString => LinearStringResource, Point => PointResource}
import com.zxventures.geladinha.infrastructure.generators.GeneratorUtil
import org.scalacheck.Gen._

trait PointOfSaleGenerator extends GeneratorUtil {

  val geom = new GeometryFactory()
  val polygonPoints = List(
    PointResource(
      latitude = -23.53560255279179f,
      longitude = -46.7936897277832f
    ),
    PointResource(
      latitude = -23.54185830797095f,
      longitude = -46.78783178329468f
    ),
    PointResource(
      latitude = -23.54059931197617f,
      longitude = -46.77781105041503f
    ),
    PointResource(
      latitude = -23.537530458746964f,
      longitude = -46.77856206893921f
    ),
    PointResource(
      latitude = -23.52997605345957f,
      longitude = -46.78401231765747f
    ),
    PointResource(
      latitude = -23.53560255279179f,
      longitude = -46.7936897277832f
    )
  )
  private val polygon1 = geom.createPolygon(polygonPoints.map(p => new Coordinate(p.longitude, p.latitude)).toArray)
  private val multiPolygon = geom.createMultiPolygon(Array(polygon1))
  multiPolygon.setSRID(4326)
  private val point = geom.createPoint(new Coordinate(-46.7859663f, -23.5344407f))
  point.setSRID(4326)

  val pointOfSaleGen = for {
    id <- posNum[Long]
    tradingName <- alphaStr(100)
    ownerName <- alphaStr(100)
    document <- alphaStr(17)
    coverageArea <- multiPolygon
    address <- point
  } yield PointOfSale(id, tradingName, ownerName, document, coverageArea, address)

  val pointOfSaleCreateGen = for {
    tradingName <- some(alphaStr(100))
    ownerName <- some(alphaStr(100))
    document <- some(alphaStr(17))
    coverageArea <- some(multiPolygon)
    address <- some(point)
  } yield PointOfSaleCreate(tradingName, ownerName, document, coverageArea, address)

  val pointOfSaleResourceGen = for {
    id <- posNum[Long]
    tradingName <- alphaStr(100)
    ownerName <- alphaStr(100)
    document <- alphaStr(17)
    coverageArea <- some(coverageAreaResource())
    address <- some(PointResource(latitude = -23.5344407f, longitude = -46.7859663f))
  } yield PointOfSaleResource(id, tradingName, ownerName, document, coverageArea, address)

  private def coverageAreaResource() = {
    MultiPolygonResource(
      List(
        PolygonResource(
          List(
            LinearStringResource(
              polygonPoints
            )
          )
        )
      )
    )
  }

}
