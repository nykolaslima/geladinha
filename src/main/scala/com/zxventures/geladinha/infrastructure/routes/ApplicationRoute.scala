package com.zxventures.geladinha.infrastructure.routes

import com.zxventures.geladinha.infrastructure.serialization.ApplicationMarshalling
import com.zxventures.geladinha.resources.{ErrorResource, RejectionsResource, UserResource}

trait ApplicationRoute extends ApplicationMarshalling with RequestIdDirective {
  implicit val userUnmarshaller = scalaPBFromRequestUnmarshaller(UserResource)
  implicit val rejectionsUnmarshaller = scalaPBFromRequestUnmarshaller(RejectionsResource)
  implicit val errorUnmarshaller = scalaPBFromRequestUnmarshaller(ErrorResource)
}
