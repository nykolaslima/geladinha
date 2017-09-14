package com.zxventures.geladinha.components.pointOfSale

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.zxventures.geladinha.components.common.CommonMapper
import com.zxventures.geladinha.components.pointOfSale.ActorMessages.{PointOfSaleCreateRequest, PointOfSaleCreateResponse, PointOfSaleLoadRequest, PointOfSaleLoadResponse}
import com.zxventures.geladinha.infrastructure.logs.GelfLogger
import com.zxventures.geladinha.infrastructure.routes.ApplicationRoute
import com.zxventures.geladinha.resources.pointOfSale.{PointOfSale => PointOfSaleResource}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait PointOfSaleRoute extends ApplicationRoute {
  implicit def actorSystem: ActorSystem
  implicit val timeout = Timeout(100.millis)
  val log = LoggerFactory.getLogger(this.getClass)
  val serviceActor = actorSystem.actorOf(PointOfSaleServiceActor.props(), PointOfSaleServiceActor.name)

  val routes = {
    extractRequestId { requestId =>
      path("points-of-sale") {
        post {
          entity(as[PointOfSaleResource]) { resource =>
            val create = PointOfSaleMapper.toPointOfSaleCreate(resource)

            val actorResponse = (serviceActor ? PointOfSaleCreateRequest(requestId, pointOfSaleCreate = create)).mapTo[PointOfSaleCreateResponse]

            onComplete(actorResponse) {
              case Success(response) =>
                response match {
                  case PointOfSaleCreateResponse(_, Some(pointOfSale), Nil, None) =>
                    val addedPointOfSale = PointOfSaleMapper.toResource(pointOfSale)
                    complete((Created, addedPointOfSale))

                  case PointOfSaleCreateResponse(_, None, messages, None) =>
                    val resource = CommonMapper.toMessagesResource(messages)
                    complete((BadRequest, resource))

                  case PointOfSaleCreateResponse(_, None, Nil, Some(failure)) =>
                    log.error(GelfLogger.buildWithRequestId(requestId).error(s"point of sale create failed"), failure)
                    complete(InternalServerError)
                }

              case Failure(e) =>
                log.error(GelfLogger.buildWithRequestId(requestId).error(s"point of sale create failed"), e)
                complete(InternalServerError)
            }
          }
        }
      } ~
      path("points-of-sale" / LongNumber) { id =>
        get {
          val actorResponse = (serviceActor ? PointOfSaleLoadRequest(requestId, id)).mapTo[PointOfSaleLoadResponse]

          onComplete(actorResponse) {
            case Success(response) =>
              response match {
                case PointOfSaleLoadResponse(_, Some(pointOfSale), _) =>
                  val resource = PointOfSaleMapper.toResource(pointOfSale)
                  complete((OK, resource))

                case PointOfSaleLoadResponse(_, None, None) =>
                  complete(NotFound)

                case PointOfSaleLoadResponse(_, None, Some(failure)) =>
                  log.error(GelfLogger.buildWithRequestId(requestId).error(s"point of sale load failed"), failure)
                  complete(InternalServerError)
              }

            case Failure(e) =>
              log.error(GelfLogger.buildWithRequestId(requestId).error(s"point of sale load failed"), e)
              complete(InternalServerError)
          }
        }
      }
    }
  }
}
