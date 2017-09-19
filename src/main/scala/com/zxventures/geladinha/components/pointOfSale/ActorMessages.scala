package com.zxventures.geladinha.components.pointOfSale

import java.util.UUID

import com.vividsolutions.jts.geom.Point
import com.zxventures.geladinha.components.common.Message

object ActorMessages {
  case class PointOfSaleCreateRequest(requestId: UUID, pointOfSaleCreate: PointOfSaleCreate, pointOfSale: Option[PointOfSale] = None)
  case class PointOfSaleCreateResponse(requestId: UUID, pointOfSale: Option[PointOfSale] = None, messages: List[Message] = List(), failure: Option[Throwable] = None)

  case class PointOfSaleLoadRequest(requestId: UUID, id: Long)
  case class PointOfSaleLoadResponse(requestId: UUID, pointOfSale: Option[PointOfSale] = None, failure: Option[Throwable] = None)

  case class PointOfSaleListRequest(requestId: UUID, address: Point)
  case class PointOfSaleListResponse(requestId: UUID, pointsOfSale: List[PointOfSale] = List(), failure: Option[Throwable] = None)
}
