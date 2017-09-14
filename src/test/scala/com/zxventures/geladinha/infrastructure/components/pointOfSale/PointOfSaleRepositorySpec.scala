package com.zxventures.geladinha.infrastructure.components.pointOfSale

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
  }
}
