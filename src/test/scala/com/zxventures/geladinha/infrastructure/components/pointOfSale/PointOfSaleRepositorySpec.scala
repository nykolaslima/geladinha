package com.zxventures.geladinha.infrastructure.components.pointOfSale

import com.zxventures.geladinha.components.pointOfSale.PointOfSaleRepository
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.IntegrationSpec

class PointOfSaleRepositorySpec extends IntegrationSpec with PointOfSaleGenerator {
  "add" when {
    "receives a point of sale" must {
      "add the point of sale" in {
        val pointOfSale = pointOfSaleGen.sample.get.copy(id = None)

        whenReady(PointOfSaleRepository.add(pointOfSale)) { result =>
          result.id should not be empty
          result.copy(id = None) shouldEqual pointOfSale
        }
      }
    }
  }
}
