package com.zxventures.geladinha.infrastructure.validation

import com.osinka.i18n.{Lang, Messages}
import com.zxventures.geladinha.infrastructure.validation.RejectionCategory._

object ValidationRulesUtil {
  implicit val userLang = Lang("pt")

  def requiredRejectionFor(target: String): Rejection = {
    Rejection(VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target))
  }
}
