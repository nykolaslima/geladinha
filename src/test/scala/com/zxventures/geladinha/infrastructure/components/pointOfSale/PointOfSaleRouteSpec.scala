package com.zxventures.geladinha.infrastructure.components.pointOfSale

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.util.Timeout
import com.zxventures.geladinha.components.pointOfSale.PointOfSaleRoute
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.RouteSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil
import com.zxventures.geladinha.resources.pointOfSale.{PointOfSale => PointOfSaleResource}
import com.zxventures.geladinha.resources.common.{Messages => MessagesResource}

import scala.concurrent.duration._

class PointOfSaleRouteSpec extends RouteSpec with PointOfSaleRoute with PointOfSaleGenerator with ValidationRulesUtil {
  def actorSystem: ActorSystem = system
  override val timeout = Timeout(5.second)

  "POST /points-of-sale" when {
    "receives a valid point of sale" must {
      "return 201 status with created point of sale" in {
        val resource = pointOfSaleResourceGen.sample.get
        Post("/points-of-sale", resource).addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual Created
          val createdPointOfSale = responseAs[PointOfSaleResource]
          createdPointOfSale.id > 0 shouldEqual true
          createdPointOfSale shouldEqual resource.copy(id = createdPointOfSale.id)
        }
      }
    }

    "receives an invalid point of sale" must {
      "return 400 status with validation messages" in {
        val resource = pointOfSaleResourceGen.sample.get.copy(ownerName = "")
        Post("/points-of-sale", resource).addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual BadRequest
          val messages = responseAs[MessagesResource]
          messages.messages.size shouldEqual 1
          messages.messages(0) shouldEqual requiredMessageResourceFor("pointOfSale.ownerName")
        }
      }
    }
  }
}
