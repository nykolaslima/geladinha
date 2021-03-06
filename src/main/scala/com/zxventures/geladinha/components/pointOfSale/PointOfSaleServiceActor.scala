package com.zxventures.geladinha.components.pointOfSale

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.zxventures.geladinha.components.pointOfSale.ActorMessages.{PointOfSaleCreateRequest, PointOfSaleCreateResponse, PointOfSaleListRequest, PointOfSaleLoadRequest}
import com.zxventures.geladinha.infrastructure.logs.ApplicationError._
import com.zxventures.geladinha.infrastructure.logs.GelfLogger
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class PointOfSaleServiceActor(repositoryActorRef: Option[ActorRef], validator: PointOfSaleValidator, mapper: PointOfSaleMapper) extends Actor with ActorLogging {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.warning(GelfLogger.warn(s"Restarting Actor due: ${reason.getMessage}",
      Map("internal_operation" -> ACTOR_RESTARTING), fullMessage = Some(reason.toString)))
  }

  val repositoryActor = createRepository()

  def receive = LoggingReceive {
    case request @ PointOfSaleCreateRequest(requestId, create, _) =>
      val replyTo = sender()

      validator.validate(create).onComplete {
        case Success(Nil) =>
          val pointOfSale = mapper.toPointOfSale(create)
          repositoryActor.tell(request.copy(pointOfSale = Some(pointOfSale)), replyTo)

        case Success(messages) =>
          replyTo ! PointOfSaleCreateResponse(requestId, messages = messages)

        case Failure(e) =>
          replyTo ! PointOfSaleCreateResponse(requestId, failure = Some(e))
      }

    case request: PointOfSaleLoadRequest =>
      repositoryActor forward request

    case request: PointOfSaleListRequest =>
      repositoryActor forward request

    case x: Any =>
      log.warning(GelfLogger.warn(s"Unknown message: $x", Map("internal_operation" -> UNKNOWN_MESSAGE)))
  }

  private def createRepository() = {
    val ref = repositoryActorRef.getOrElse(context.actorOf(PointOfSaleRepositoryActor.props(), PointOfSaleRepositoryActor.name))
    context.watch(ref)
    ref
  }
}

object PointOfSaleServiceActor {
  def props(repositoryActorRef: Option[ActorRef] = None, validator: PointOfSaleValidator = PointOfSaleValidator, mapper: PointOfSaleMapper = PointOfSaleMapper) = Props(new PointOfSaleServiceActor(repositoryActorRef, validator, mapper))
  val name = "point-of-sale-service-actor"
}
