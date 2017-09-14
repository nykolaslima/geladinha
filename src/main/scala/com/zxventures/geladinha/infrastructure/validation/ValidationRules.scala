package com.zxventures.geladinha.infrastructure.validation

import com.osinka.i18n.{Lang, Messages}
import com.zxventures.geladinha.components.common.Message
import com.zxventures.geladinha.components.common.MessageCategory._

trait ValidationRules {
  implicit val userLang = Lang("pt")

  def notEmpty[T](value: String, target: String): (T) => (Boolean, Message) = {
    obj => (value.trim.nonEmpty, Message(VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target)))
  }

  def notEmpty[T](value: Option[String], target: String): (T) => (Boolean, Message) = {
    obj => (value.isDefined && value.get.trim.nonEmpty, Message(VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target)))
  }

  def notEmptyOption[T](value: Option[Any], target: String): (T) => (Boolean, Message) = {
    obj => (value.isDefined, Message(VALIDATION, target, Messages("validation.required", Messages(target)), "validation.required", List(target)))
  }

  def ensure[T](condition: Boolean, target: String, i18nKey: String): (T) => (Boolean, Message) = {
    obj => (condition, Message(VALIDATION, target, Messages(i18nKey), i18nKey))
  }

  def between[T](value: Double, min: Double, max: Double, target: String): (T) => (Boolean, Message) = {
    obj => (value >= min && value <= max, Message(VALIDATION, target, Messages("validation.between", Messages(target), min, max), "validation.between", List(target, min, max)))
  }
}
