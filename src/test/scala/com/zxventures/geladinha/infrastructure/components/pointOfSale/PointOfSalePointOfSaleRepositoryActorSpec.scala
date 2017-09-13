package com.zxventures.geladinha.infrastructure.components.pointOfSale

import java.util.UUID

import com.zxventures.geladinha.components.pointOfSale.ActorMessages.{PointOfSaleCreateRequest, PointOfSaleCreateResponse}
import com.zxventures.geladinha.components.pointOfSale.{PointOfSaleRepository, PointOfSaleRepositoryActor}
import com.zxventures.geladinha.infrastructure.generators.pointOfSale.PointOfSaleGenerator
import com.zxventures.geladinha.infrastructure.test.ActorSpec
import com.zxventures.geladinha.infrastructure.validation.ValidationRulesUtil

import scala.concurrent.Future

class PointOfSalePointOfSaleRepositoryActorSpec extends ActorSpec with PointOfSaleGenerator with ValidationRulesUtil {
  "PointOfSaleCreateRequest message" when {
    "point of sale is successfully added" must {
      "reply added point of sale to sender" in {
        val (repository, pointOfSaleRepositoryActor) = setUp()
        val requestId = UUID.randomUUID()
        val pointOfSaleCreate = pointOfSaleCreateGen.sample.get
        val pointOfSale = pointOfSaleGen.sample.get
        val createMessage = PointOfSaleCreateRequest(requestId, pointOfSaleCreate, Some(pointOfSale))
        (repository.add _).when(pointOfSale).returns(Future.successful(pointOfSale))

        pointOfSaleRepositoryActor ! createMessage

        val response = PointOfSaleCreateResponse(requestId, pointOfSale = Some(pointOfSale))
        expectMsg(response)
      }
    }

    "point of sale add failed" must {
      "reply failure to sender" in {
        val (repository, pointOfSaleRepositoryActor) = setUp()
        val requestId = UUID.randomUUID()
        val pointOfSaleCreate = pointOfSaleCreateGen.sample.get
        val pointOfSale = pointOfSaleGen.sample.get
        val createMessage = PointOfSaleCreateRequest(requestId, pointOfSaleCreate, Some(pointOfSale))
        val exception = new RuntimeException("Failed to add point of sale")
        (repository.add _).when(pointOfSale).returns(Future.failed(exception))

        pointOfSaleRepositoryActor ! createMessage

        val response = PointOfSaleCreateResponse(requestId, failure = Some(exception))
        expectMsg(response)
      }
    }
  }

  private def setUp() = {
    val repositoryMock = stub[PointOfSaleRepository]
    val pointOfSaleRepositoryActor = system.actorOf(PointOfSaleRepositoryActor.props(repositoryMock))

    (repositoryMock, pointOfSaleRepositoryActor)
  }
}
