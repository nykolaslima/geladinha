package com.zxventures.geladinha.infrastructure.validation

import com.osinka.i18n.{Lang, Messages}
import com.zxventures.geladinha.components.common.Message
import com.zxventures.geladinha.components.common.MessageCategory
import com.zxventures.geladinha.resources.common.{Message => MessageResource, MessageCategory => MessageCategoryResource}

trait ValidationRulesUtil {
  implicit val userLang = Lang("pt")

  def requiredMessageFor(target: String) = Message(
    MessageCategory.VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target)
  )

  def requiredMessageResourceFor(target: String) = MessageResource(
    MessageCategoryResource.VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target)
  )
}
