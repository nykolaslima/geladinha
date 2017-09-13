package com.zxventures.geladinha.infrastructure.components.pointOfSale

import com.zxventures.geladinha.components.pointOfSale.PointOfSaleValidator
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.UnitSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil

class PointOfSaleValidatorSpec extends UnitSpec with ValidationRulesUtil with PointOfSaleGenerator {

  "validate" when {
    "point of sale with no trading name" must {
      "return required message" in {
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(tradingName = None)
        val messages = PointOfSaleValidator.validate(pointOfSale)

        messages.size shouldEqual 1
        messages(0) shouldEqual requiredMessageFor("pointOfSale.tradingName")
      }
    }

    "point of sale with no owner name" must {
      "return required message" in {
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(ownerName = None)
        val messages = PointOfSaleValidator.validate(pointOfSale)

        messages.size shouldEqual 1
        messages(0) shouldEqual requiredMessageFor("pointOfSale.ownerName")
      }
    }

    "point of sale with no document name" must {
      "return required message" in {
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(document = None)
        val messages = PointOfSaleValidator.validate(pointOfSale)

        messages.size shouldEqual 1
        messages(0) shouldEqual requiredMessageFor("pointOfSale.document")
      }
    }

    "point of sale with no coverage area" must {
      "return required message" in {
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(coverageArea = None)
        val messages = PointOfSaleValidator.validate(pointOfSale)

        messages.size shouldEqual 1
        messages(0) shouldEqual requiredMessageFor("pointOfSale.coverageArea")
      }
    }

    "point of sale with no address" must {
      "return required message" in {
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(address = None)
        val messages = PointOfSaleValidator.validate(pointOfSale)

        messages.size shouldEqual 1
        messages(0) shouldEqual requiredMessageFor("pointOfSale.address")
      }
    }
  }

}
