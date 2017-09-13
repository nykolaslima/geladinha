package com.zxventures.geladinha.components.pointOfSale

import com.zxventures.geladinha.components.common.Message
import com.zxventures.geladinha.infrastructure.validation.{Rule, ValidationRules, Validator => DefaultValidator}

trait PointOfSaleValidator extends ValidationRules {

  def validate(pointOfSale: PointOfSaleCreate): List[Message] = {
    val rules: List[Rule[PointOfSaleCreate]] = List(
      Rule(notEmpty(pointOfSale.tradingName, "pointOfSale.tradingName")),
      Rule(notEmpty(pointOfSale.ownerName, "pointOfSale.ownerName")),
      Rule(notEmpty(pointOfSale.document, "pointOfSale.document")),
      Rule(notEmptyOption(pointOfSale.coverageArea, "pointOfSale.coverageArea")),
      Rule(notEmptyOption(pointOfSale.address, "pointOfSale.address"))
    )

    DefaultValidator.validate(pointOfSale, rules)
  }

}

object PointOfSaleValidator extends PointOfSaleValidator
