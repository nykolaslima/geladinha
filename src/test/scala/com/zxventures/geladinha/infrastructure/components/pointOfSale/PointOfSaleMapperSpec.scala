package com.zxventures.geladinha.infrastructure.components.pointOfSale

import com.vividsolutions.jts.geom.MultiPolygon
import com.zxventures.geladinha.components.pointOfSale.PointOfSaleMapper
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.UnitSpec
import com.zxventures.geladinha.resources.geometry.{MultiPolygon => MultiPolygonResource}

class PointOfSaleMapperSpec extends UnitSpec with PointOfSaleGenerator {

  "toPointOfSale" when {
    "receives a valid point of sale create" must {
      "transform into point of sale" in {
        val pointOfSaleCreate = pointOfSaleCreateGen.sample.get
        val pointOfSale = PointOfSaleMapper.toPointOfSale(pointOfSaleCreate)

        pointOfSale.id shouldEqual 0
        pointOfSale.tradingName shouldEqual pointOfSaleCreate.tradingName.get
        pointOfSale.ownerName shouldEqual pointOfSaleCreate.ownerName.get
        pointOfSale.document shouldEqual pointOfSaleCreate.document.get
        pointOfSale.coverageArea shouldEqual pointOfSaleCreate.coverageArea.get
        pointOfSale.address shouldEqual pointOfSaleCreate.address.get
      }
    }
  }

  "toPointOfSaleCreate" when {
    "receives a valid point of sale resource" must {
      "transform into point of sale create" in {
        val resource = pointOfSaleResourceGen.sample.get
        val pointOfSaleCreate = PointOfSaleMapper.toPointOfSaleCreate(resource)

        pointOfSaleCreate.tradingName.get shouldEqual resource.tradingName
        pointOfSaleCreate.ownerName.get shouldEqual resource.ownerName
        pointOfSaleCreate.document.get shouldEqual resource.document
        multiPolygonShouldEqual(pointOfSaleCreate.coverageArea.get, resource.coverageArea.get)
        pointOfSaleCreate.address.get.getY.toFloat shouldEqual resource.address.get.latitude
        pointOfSaleCreate.address.get.getX.toFloat shouldEqual resource.address.get.longitude
      }
    }
  }

  "toResource" when {
    "receives a valid point of sale" must {
      "transform into point of sale resource" in {
        val pointOfSale = pointOfSaleGen.sample.get
        val resource = PointOfSaleMapper.toResource(pointOfSale)

        resource.id shouldEqual pointOfSale.id
        resource.tradingName shouldEqual pointOfSale.tradingName
        resource.ownerName shouldEqual pointOfSale.ownerName
        resource.document shouldEqual pointOfSale.document
        multiPolygonShouldEqual(pointOfSale.coverageArea, resource.coverageArea.get)
        resource.address.get.latitude shouldEqual pointOfSale.address.getY
        resource.address.get.longitude shouldEqual pointOfSale.address.getX
      }
    }
  }

  private def multiPolygonShouldEqual(multiPolygon: MultiPolygon, resource: MultiPolygonResource) = {
    multiPolygon.getCoordinates.toList.map(c => (c.x, c.y)) shouldEqual resource.polygons(0).linearStrings(0).points.map(p => (p.longitude, p.latitude))
  }

}
