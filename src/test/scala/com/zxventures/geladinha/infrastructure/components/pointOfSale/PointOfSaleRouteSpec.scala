package com.zxventures.geladinha.infrastructure.components.pointOfSale

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.util.Timeout
import com.vividsolutions.jts.geom.Coordinate
import com.zxventures.geladinha.components.pointOfSale.{PointOfSaleMapper, PointOfSaleRepository, PointOfSaleRoute}
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.RouteSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil
import com.zxventures.geladinha.resources.pointOfSale.{PointOfSale => PointOfSaleResource, PointsOfSale => PointsOfSaleResource}
import com.zxventures.geladinha.resources.common.{Messages => MessagesResource}

import scala.concurrent.Await
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

  "GET /points-of-sale/{id}" when {
    "receives a existing id" must {
      "return 200 status with given point of sale" in {
        val pointOfSale = pointOfSaleGen.sample.get
        val idToFind = Await.result(PointOfSaleRepository.add(pointOfSale), 5.seconds).id

        Get(s"/points-of-sale/${idToFind}").addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual OK
          val loadedPointOfSale = responseAs[PointOfSaleResource]
          loadedPointOfSale shouldEqual PointOfSaleMapper.toPointOfSaleResource(pointOfSale.copy(id = idToFind))
        }
      }
    }

    "receives a nonexistent id" must {
      "return 404 status" in {
        Get(s"/points-of-sale/1").addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual NotFound
        }
      }
    }
  }

  /**
    * The used polygon and points of sale's addresses can be viewed here: https://gist.github.com/anonymous/6f91f2d01a3058a702b99de3b9a5656e
    */
  "GET /points-of-sale" when {
    "receives a latitude and longitude that is in point of sale coverage area" must {
      "return 200 status with available point of sale ordered by distance" in {
        val furtherPointOfSaleAddress = geom.createPoint(new Coordinate(-46.781759262084954, -23.53827800651484))
        furtherPointOfSaleAddress.setSRID(4326)
        val closestPointOfSaleAddress = geom.createPoint(new Coordinate(-46.78534269332886, -23.53483531705201))
        closestPointOfSaleAddress.setSRID(4326)

        val furtherPointOfSale = pointOfSaleGen.sample.get.copy(address = furtherPointOfSaleAddress)
        val furtherPointOfSaleId = Await.result(PointOfSaleRepository.add(furtherPointOfSale), 5.seconds).id
        val closestPointOfSale = pointOfSaleGen.sample.get.copy(address = closestPointOfSaleAddress)
        val closestPointOfSaleId = Await.result(PointOfSaleRepository.add(closestPointOfSale), 5.seconds).id

        Get(s"/points-of-sale?latitude=-23.534186114084868&longitude=-46.78607225418091").addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual OK
          val listedPointsOfSale = responseAs[PointsOfSaleResource]

          listedPointsOfSale shouldEqual PointOfSaleMapper.toPointsOfSaleResource(List(
            closestPointOfSale.copy(id = closestPointOfSaleId),
            furtherPointOfSale.copy(id = furtherPointOfSaleId)
          ))
        }
      }
    }

    "receives a latitude and longitude that isn't in point of sale coverage area" must {
      "return 200 status with no points of sale" in {
        val pointOfSale = pointOfSaleGen.sample.get
        Await.result(PointOfSaleRepository.add(pointOfSale), 5.seconds)

        Get(s"/points-of-sale?latitude=-23.533064755959444&longitude=-46.78025722503662").addHeader(acceptProto).addHeader(contentProto) ~> routes ~> check {
          status shouldEqual OK
          val listedPointsOfSale = responseAs[PointsOfSaleResource]

          listedPointsOfSale.pointsOfSale.size shouldEqual 0
        }
      }
    }
  }
}
