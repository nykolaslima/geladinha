package com.zxventures.geladinha.infrastructure.components.pointOfSale

import com.zxventures.geladinha.components.pointOfSale.{PointOfSaleRepository, PointOfSaleValidator}
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.UnitSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Future

class PointOfSaleValidatorSpec extends UnitSpec with ValidationRulesUtil with PointOfSaleGenerator with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(100, Millis))

  "validate" when {
    "point of sale with no trading name" must {
      "return required message" in {
        val(repository, validator) = setUp()
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(tradingName = None)
        (repository.loadByDocument _).when(*).returns(Future.successful(None))

        whenReady(validator.validate(pointOfSale)) { messages =>
          messages.size shouldEqual 1
          messages(0) shouldEqual requiredMessageFor("pointOfSale.tradingName")
        }
      }
    }

    "point of sale with no owner name" must {
      "return required message" in {
        val(repository, validator) = setUp()
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(ownerName = None)
        (repository.loadByDocument _).when(*).returns(Future.successful(None))

        whenReady(validator.validate(pointOfSale)) { messages =>
          messages.size shouldEqual 1
          messages(0) shouldEqual requiredMessageFor("pointOfSale.ownerName")
        }
      }
    }

    "point of sale with no document name" must {
      "return required message" in {
        val(repository, validator) = setUp()
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(document = None)
        (repository.loadByDocument _).when(*).returns(Future.successful(None))

        whenReady(validator.validate(pointOfSale)) { messages =>
          messages.size shouldEqual 1
          messages(0) shouldEqual requiredMessageFor("pointOfSale.document")
        }
      }
    }

    "point of sale with document that already exists" must {
      "return document already exists message" in {
        val(repository, validator) = setUp()
        val pointOfSale = pointOfSaleCreateGen.sample.get
        val alreadyExistentPointOfSale = pointOfSaleGen.sample.get
        (repository.loadByDocument _).when(pointOfSale.document.get).returns(Future.successful(Some(alreadyExistentPointOfSale)))

        whenReady(validator.validate(pointOfSale)) { messages =>
          messages.size shouldEqual 1
          messages(0) shouldEqual ensureMessageFor("pointOfSale.document", "validation.pointOfSale.documentAlreadyInUse")
        }
      }
    }

    "point of sale with no coverage area" must {
      "return required message" in {
        val(repository, validator) = setUp()
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(coverageArea = None)
        (repository.loadByDocument _).when(*).returns(Future.successful(None))

        whenReady(validator.validate(pointOfSale)) { messages =>
          messages.size shouldEqual 1
          messages(0) shouldEqual requiredMessageFor("pointOfSale.coverageArea")
        }
      }
    }

    "point of sale with no address" must {
      "return required message" in {
        val(repository, validator) = setUp()
        val pointOfSale = pointOfSaleCreateGen.sample.get.copy(address = None)
        (repository.loadByDocument _).when(*).returns(Future.successful(None))

        whenReady(validator.validate(pointOfSale)) { messages =>
          messages.size shouldEqual 1
          messages(0) shouldEqual requiredMessageFor("pointOfSale.address")
        }
      }
    }
  }

  private def setUp() = {
    val repositoryMock = stub[PointOfSaleRepository]
    val validator = new PointOfSaleValidator {
      override val repository = repositoryMock
    }

    (repositoryMock, validator)
  }

}
