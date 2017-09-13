package com.zxventures.geladinha.components.pointOfSale

import com.vividsolutions.jts.geom._
import com.zxventures.geladinha.resources.geometry.{LinearString, MultiPolygon => MultiPolygonResource, Point => PointResource, Polygon => PolygonResource}
import com.zxventures.geladinha.resources.pointOfSale.{PointOfSale => PointOfSaleResource}

trait PointOfSaleMapper {
  def toPointOfSale(pointOfSaleCreate: PointOfSaleCreate) = PointOfSale(
    id = None,
    tradingName = pointOfSaleCreate.tradingName.get,
    ownerName = pointOfSaleCreate.ownerName.get,
    document = pointOfSaleCreate.document.get,
    coverageArea = pointOfSaleCreate.coverageArea.get,
    address = pointOfSaleCreate.address.get
  )

  def toPointOfSaleCreate(resource: PointOfSaleResource) = PointOfSaleCreate(
    tradingName = nonEmpty(resource.tradingName),
    ownerName = nonEmpty(resource.ownerName),
    document = nonEmpty(resource.document),
    coverageArea = resource.coverageArea.map(toCoverageArea),
    address = resource.address.map(toPoint)
  )

  def toResource(pointOfSale: PointOfSale) = PointOfSaleResource(
    id = pointOfSale.id.get,
    tradingName = pointOfSale.tradingName,
    ownerName = pointOfSale.ownerName,
    document = pointOfSale.document,
    coverageArea = Some(toCoverageAreaResource(pointOfSale.coverageArea)),
    address = Some(toPointResource(pointOfSale.address))
  )

  private def toCoverageAreaResource(coverageArea: MultiPolygon): MultiPolygonResource = {
    val polygonQuantity = coverageArea.getNumGeometries

    val polygons: Seq[PolygonResource] = for {
      i <- 0 until polygonQuantity
      polygon = coverageArea.getGeometryN(i).asInstanceOf[Polygon]
      polygonResourcePoints = polygon.getCoordinates
        .map(c => (c.x, c.y))
        .map {case (longitude, latitude) => PointResource(latitude.toFloat, longitude.toFloat)}
      linearString = LinearString(polygonResourcePoints)
    } yield PolygonResource(List(linearString))

    MultiPolygonResource(polygons)
  }

  private def toPointResource(point: Point) = PointResource(
    latitude = point.getY.toFloat,
    longitude = point.getX.toFloat
  )

  private def toCoverageArea(resource: MultiPolygonResource): MultiPolygon = {
    val geom = new GeometryFactory()

    val polygons = resource.polygons.map { polygon =>
       val linerStringsCoordinates = polygon.linearStrings.map { linearRing =>
        linearRing.points.map { point =>
          new Coordinate(point.longitude, point.latitude)
        }
      }

      if(linerStringsCoordinates.size > 1) throw new UnsupportedOperationException("Polygon with exterior rings are not supported because we are using Earth as our exterior ring (https://tools.ietf.org/html/rfc7946#appendix-A.3)")

      linerStringsCoordinates.headOption.getOrElse(List()).toArray
    }.map(geom.createPolygon)

    val multiPolygon = geom.createMultiPolygon(polygons.toArray)
    multiPolygon.setSRID(4326)

    multiPolygon
  }

  private def toPoint(resource: PointResource): Point = {
    val geom = new GeometryFactory()

    val point = geom.createPoint(new Coordinate(resource.longitude, resource.latitude))
    point.setSRID(4326)

    point
  }

  private def nonEmpty(value: String) = {
    if(value.trim.isEmpty) {
      None
    } else {
      Some(value.trim)
    }
  }
}

object PointOfSaleMapper extends PointOfSaleMapper
