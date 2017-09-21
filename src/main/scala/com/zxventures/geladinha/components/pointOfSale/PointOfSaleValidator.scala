package com.zxventures.geladinha.components.pointOfSale

import com.zxventures.geladinha.components.common.Message
import com.zxventures.geladinha.infrastructure.validation.{Rule, ValidationRules, Validator => DefaultValidator}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait PointOfSaleValidator extends ValidationRules {

  val repository: PointOfSaleRepository = PointOfSaleRepository

  def validate(pointOfSale: PointOfSaleCreate): Future[List[Message]] = {
    repository.loadByDocument(pointOfSale.document.getOrElse("")).map { documentPos =>
      val rules: List[Rule[PointOfSaleCreate]] = List(
        Rule(notEmpty(pointOfSale.tradingName, "pointOfSale.tradingName")),
        Rule(notEmpty(pointOfSale.ownerName, "pointOfSale.ownerName")),
        Rule(ensure(documentPos.isEmpty, "pointOfSale.document", "validation.pointOfSale.documentAlreadyInUse")),
        Rule(notEmpty(pointOfSale.document, "pointOfSale.document")),
        Rule(notEmptyOption(pointOfSale.coverageArea, "pointOfSale.coverageArea")),
        Rule(notEmptyOption(pointOfSale.address, "pointOfSale.address"))
      )

      DefaultValidator.validate(pointOfSale, rules)
    }
  }

}

object PointOfSaleValidator extends PointOfSaleValidator
