package com.zxventures.geladinha.components.user

import com.zxventures.geladinha.infrastructure.generators.user.UserGenerator
import com.zxventures.geladinha.infrastructure.test.UnitSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil._

class UserValidatorSpec extends UnitSpec with UserGenerator {

  "The UserValidator" must {
    "require name" in {
      val user = userGen.sample.get.copy(name = "")
      val rejections = UserValidator.validate(user)

      rejections.size shouldEqual 1
      rejections(0) shouldEqual requiredRejectionFor("user.name")
    }
  }

}
