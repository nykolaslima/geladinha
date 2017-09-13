package com.zxventures.geladinha.infrastructure.validation

import com.zxventures.geladinha.components.common.Message
import com.zxventures.geladinha.components.common.MessageCategory._
import com.zxventures.geladinha.infrastructure.test.UnitSpec

class ValidatorSpec extends UnitSpec {

  "The Validator" must {
    "validate rule" in {
      val emptyStringToBeValidated = ""
      val rules: List[Rule[String]] = List(
        Rule(value => (!value.isEmpty, Message(VALIDATION, "name", "name can't be empty", "name")))
      )

      val rejections = Validator.validate(emptyStringToBeValidated, rules)

      rejections.size shouldBe 1
      rejections.head.category shouldBe VALIDATION
      rejections.head.target shouldBe "name"
      rejections.head.message shouldBe "name can't be empty"
    }

    "validate multiple rules" in {
      val passwordToBeValidated = "12345"
      val rules: List[Rule[String]] = List(
        Rule(value => (value.size >= 6, Message(VALIDATION, "password", "password should have at least 6 characters", "password"))),
        Rule(value => (value.matches("[a-zA-Z]") && value.matches("[0-9]"), Message(VALIDATION, "password", "password should have numbers and letters", "password")))
      )

      val rejections = Validator.validate(passwordToBeValidated, rules)

      rejections.size shouldBe 2
      rejections(0).category shouldBe VALIDATION
      rejections(0).target shouldBe "password"
      rejections(0).message shouldBe "password should have at least 6 characters"
      rejections(1).category shouldBe VALIDATION
      rejections(1).target shouldBe "password"
      rejections(1).message shouldBe "password should have numbers and letters"
    }

    "return empty list for no rules" in {
      val value = "12345"
      val rules: List[Rule[String]] = List()

      val rejections = Validator.validate(value, rules)

      rejections shouldBe empty
    }

    "return empty list for valid value" in {
      val passwordToBeValidated = "123456"
      val rules: List[Rule[String]] = List(
        Rule(value => (value.size >= 6, Message(VALIDATION, "password", "password should have at least 6 characters", "password")))
      )

      val rejections = Validator.validate(passwordToBeValidated, rules)

      rejections shouldBe empty
    }
  }

}
