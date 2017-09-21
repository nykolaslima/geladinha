package com.zxventures.geladinha.components.pointOfSale

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import com.zxventures.geladinha.components.pointOfSale.ActorMessages._
import com.zxventures.geladinha.infrastructure.logs.ApplicationError._
import com.zxventures.geladinha.infrastructure.logs.GelfLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class PointOfSaleRepositoryActor(repository: PointOfSaleRepository) extends Actor with ActorLogging {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.warning(GelfLogger.warn(s"Restarting Actor due: ${reason.getMessage}",
      Map("internal_operation" -> ACTOR_RESTARTING), fullMessage = Some(reason.toString)))
  }

  def receive = LoggingReceive {
    case PointOfSaleCreateRequest(requestId, _, Some(pointOfSale)) =>
      val replyTo = sender()

      repository.add(pointOfSale).onComplete {
        case Success(addedPointOfSale) =>
          replyTo ! PointOfSaleCreateResponse(requestId, Some(addedPointOfSale))

        case Failure(e) =>
          log.error(e, GelfLogger.buildWithRequestId(requestId).error(s"point of sale create failed", Map("action" -> "point-of-sale-create", "result" -> "failed")))
          replyTo ! PointOfSaleCreateResponse(requestId, failure = Some(e))
      }

    case PointOfSaleLoadRequest(requestId, id) =>
      val replyTo = sender()

      repository.load(id).onComplete {
        case Success(pointOfSale) =>
          replyTo ! PointOfSaleLoadResponse(requestId, pointOfSale)

        case Failure(e) =>
          log.error(e, GelfLogger.buildWithRequestId(requestId).error(s"point of sale load failed", Map("action" -> "point-of-sale-load", "result" -> "failed")))
          replyTo ! PointOfSaleLoadResponse(requestId, failure = Some(e))
      }

    case PointOfSaleListRequest(requestId, address) =>
      val replyTo = sender()

      repository.list(address).onComplete {
        case Success(pointsOfSale) =>
          replyTo ! PointOfSaleListResponse(requestId, pointsOfSale)

        case Failure(e) =>
          log.error(e, GelfLogger.buildWithRequestId(requestId).error(s"points of sale list available failed", Map("action" -> "point-of-sale-list-available", "result" -> "failed")))
          replyTo ! PointOfSaleListResponse(requestId, failure = Some(e))
      }

    case x: Any =>
      log.warning(GelfLogger.warn(s"Unknown message: $x", Map("internal_operation" -> UNKNOWN_MESSAGE)))
  }
}

object PointOfSaleRepositoryActor {
  def props(pointOfSaleRepository: PointOfSaleRepository = PointOfSaleRepository) = Props(new PointOfSaleRepositoryActor(pointOfSaleRepository))
  val name = "point-of-sale-repository-actor"

}
