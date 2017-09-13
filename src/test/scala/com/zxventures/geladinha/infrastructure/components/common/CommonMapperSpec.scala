package com.zxventures.geladinha.infrastructure.components.common

import com.zxventures.geladinha.components.common.CommonMapper
import com.zxventures.geladinha.infrastructure.test.UnitSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil

class CommonMapperSpec extends UnitSpec with ValidationRulesUtil {
  "toResource" when {
    "receive valid messages" must {
      "transform into messages resource" in {
        val message = requiredMessageFor("pointOfSale.ownerName")
        val resource = CommonMapper.toMessagesResource(List(message))

        resource.messages.size shouldEqual 1
        resource.messages(0).category.name shouldEqual message.category.toString
        resource.messages(0).target shouldEqual message.target
        resource.messages(0).message shouldEqual message.message
        resource.messages(0).key shouldEqual message.key
        resource.messages(0).args shouldEqual message.args
      }
    }
  }
}
