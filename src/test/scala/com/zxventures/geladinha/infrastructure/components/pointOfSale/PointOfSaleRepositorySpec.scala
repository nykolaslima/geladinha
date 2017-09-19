package com.zxventures.geladinha.infrastructure.components.pointOfSale

import com.vividsolutions.jts.geom.Coordinate
import com.zxventures.geladinha.components.pointOfSale.PointOfSaleRepository
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.IntegrationSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class PointOfSaleRepositorySpec extends IntegrationSpec with PointOfSaleGenerator {
  "add" when {
    "receives a point of sale" must {
      "add the point of sale" in {
        val pointOfSale = pointOfSaleGen.sample.get.copy(id = 0)

        whenReady(PointOfSaleRepository.add(pointOfSale)) { result =>
          result.id > 0 shouldEqual true
          result shouldEqual pointOfSale.copy(id = result.id)
        }
      }
    }
  }

  "load" when {
    "receives an existing id" must {
      "return the point of sale for given id" in {
        val pointOfSale = pointOfSaleGen.sample.get
        val idToFind = Await.result(PointOfSaleRepository.add(pointOfSale), 5.seconds).id

        whenReady(PointOfSaleRepository.load(idToFind)) { result =>
          result shouldEqual Some(pointOfSale.copy(id = idToFind))
        }
      }
    }

    "receives a non existent id" must {
      "return empty response" in {
        val idToFind = 1
        whenReady(PointOfSaleRepository.load(idToFind)) { result =>
          result shouldEqual None
        }
      }
    }
  }

  /**
    * The used polygon and points of sale's addresses can be viewed here: https://gist.github.com/anonymous/6f91f2d01a3058a702b99de3b9a5656e
    */
  "list" when {
    "address is in points of sale's coverage area" must {
      "return the points of sale available to that address ordered by proximity" in {
        val address = geom.createPoint(new Coordinate(-46.78607225418091, -23.534186114084868))
        address.setSRID(4326)
        val furtherPointOfSaleAddress = geom.createPoint(new Coordinate(-46.781759262084954, -23.53827800651484))
        furtherPointOfSaleAddress.setSRID(4326)
        val closestPointOfSaleAddress = geom.createPoint(new Coordinate(-46.78534269332886, -23.53483531705201))
        closestPointOfSaleAddress.setSRID(4326)

        val furtherPointOfSale = pointOfSaleGen.sample.get.copy(address = furtherPointOfSaleAddress)
        val furtherPointOfSaleId = Await.result(PointOfSaleRepository.add(furtherPointOfSale), 5.seconds).id
        val closestPointOfSale = pointOfSaleGen.sample.get.copy(address = closestPointOfSaleAddress)
        val closestPointOfSaleId = Await.result(PointOfSaleRepository.add(closestPointOfSale), 5.seconds).id

        whenReady(PointOfSaleRepository.list(address)) { result =>
          result shouldEqual List(
            closestPointOfSale.copy(id = closestPointOfSaleId),
            furtherPointOfSale.copy(id = furtherPointOfSaleId)
          )
        }
      }
    }

    "address isn't in point of sale's coverage area" must {
      "return no points of sale" in {
        val point = geom.createPoint(new Coordinate(-46.78025722503662, -23.533064755959444))
        point.setSRID(4326)
        val pointOfSale = pointOfSaleGen.sample.get
        Await.result(PointOfSaleRepository.add(pointOfSale), 5.seconds)

        whenReady(PointOfSaleRepository.list(point)) { result =>
          result shouldEqual List()
        }
      }
    }
  }
}
