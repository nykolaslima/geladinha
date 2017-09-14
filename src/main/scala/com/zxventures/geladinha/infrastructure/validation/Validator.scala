package com.zxventures.geladinha.infrastructure.validation

import algebra.Semigroup
import cats.data.Validated._
import cats.data.ValidatedNel
import cats.implicits._
import com.zxventures.geladinha.components.common.Message

object Validator {
  def validate[T](value: T, rules: List[Rule[T]]): List[Message] = {
    implicit val semigroup = new Semigroup[T] {
      override def combine(x: T, y: T): T = x
    }

    def executeRule(rule: Rule[T]): ValidatedNel[Message, T] = rule.validate(value) match {
      case (condition, message) => if (condition) valid(value) else invalidNel(message)
    }

    val result = rules match {
      case x :: xs => xs.foldLeft(executeRule(x)) { case (z, n) => z.combine(executeRule(n)) }
      case Nil => valid(value)
    }

    result match {
      case Valid(v) => List()
      case Invalid(rejections) => rejections.unwrap
    }
  }
}

case class Rule[T](validate: T => (Boolean, Message))
