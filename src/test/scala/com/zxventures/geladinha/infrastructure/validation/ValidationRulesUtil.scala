package com.zxventures.geladinha.infrastructure.validation

import com.zxventures.geladinha.infrastructure.validation.RejectionCategory._
import com.zxventures.geladinha.resources.RejectionResource
import com.osinka.i18n.{Lang, Messages}

object ValidationRulesUtil {
  implicit val userLang = Lang("pt")

  def requiredRejectionFor(target: String): Rejection = {
    Rejection(VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target))
  }

  def requiredRejectionResourceFor(target: String): RejectionResource = {
    val rejection = requiredRejectionFor(target)
    RejectionResource(
      rejection.category.toString,
      rejection.target,
      rejection.message,
      rejection.key,
      rejection.args.map(_.toString)
    )
  }
}
