package com.zxventures.geladinha.infrastructure.components.pointOfSale

import java.util.UUID

import akka.testkit.TestProbe
import com.zxventures.geladinha.components.pointOfSale.ActorMessages.{PointOfSaleCreateRequest, PointOfSaleCreateResponse, PointOfSaleLoadRequest}
import com.zxventures.geladinha.components.pointOfSale.{PointOfSaleMapper, PointOfSaleServiceActor, PointOfSaleValidator}
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.ActorSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil

class PointOfSaleServiceActorSpec extends ActorSpec with PointOfSaleGenerator with ValidationRulesUtil {
  "PointOfSaleCreateRequest message" when {
    "point of sale create is valid" must {
      "forward message to PointOfSaleRepositoryActor" in {
        val (repositoryActor, validator, mapper, serviceActor) = setUp()
        val pointOfSaleCreate = pointOfSaleCreateGen.sample.get
        val pointOfSale = pointOfSaleGen.sample.get
        val createMessage = PointOfSaleCreateRequest(UUID.randomUUID(), pointOfSaleCreate = pointOfSaleCreate)
        (validator.validate _).when(pointOfSaleCreate).returns(List())
        (mapper.toPointOfSale _).when(pointOfSaleCreate).returns(pointOfSale)

        serviceActor ! createMessage

        repositoryActor.expectMsg(createMessage.copy(pointOfSale = Some(pointOfSale)))
      }
    }

    "point of sale is invalid" must {
      "reply to sender with validation messages" in {
        val (_, validator, _, serviceActor) = setUp()
        val requestId = UUID.randomUUID()
        val pointOfSaleCreate = pointOfSaleCreateGen.sample.get
        val messages = List(requiredMessageFor("pointOfSale.ownerName"))
        val createMessage = PointOfSaleCreateRequest(requestId, pointOfSaleCreate = pointOfSaleCreate)
        (validator.validate _).when(pointOfSaleCreate).returns(messages)

        serviceActor ! createMessage

        val response = PointOfSaleCreateResponse(requestId, None, messages)
        expectMsg(response)
      }
    }
  }

  "PointOfSaleLoadRequest message" when {
    "receives a request" must {
      "forward message to PointOfSaleRepositoryActor" in {
        val (repositoryActor, _, _, serviceActor) = setUp()
        val id = 1l
        val loadMessage = PointOfSaleLoadRequest(UUID.randomUUID(), id)

        serviceActor ! loadMessage

        repositoryActor.expectMsg(loadMessage)
      }
    }
  }

  private def setUp() = {
    val repositoryActor = TestProbe()
    val validatorMock = stub[PointOfSaleValidator]
    val mapperMock = stub[PointOfSaleMapper]
    val serviceActor = system.actorOf(PointOfSaleServiceActor.props(Some(repositoryActor.ref), validatorMock, mapperMock))

    (repositoryActor, validatorMock, mapperMock, serviceActor)
  }
}
