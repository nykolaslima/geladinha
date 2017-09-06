package com.zxventures.geladinha.infrastructure.generators.user

import com.zxventures.geladinha.components.user.User
import com.zxventures.geladinha.infrastructure.generators.GeneratorUtil
import java.util.UUID

trait UserGenerator extends GeneratorUtil {

  val userGen = for {
    id <- some(UUID.randomUUID())
    name <- alphaStr(30)
  } yield User(id, name)

}
