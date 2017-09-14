package com.zxventures.geladinha.components.pointOfSale

import java.util.UUID

import com.zxventures.geladinha.components.common.Message

object ActorMessages {
  case class PointOfSaleCreateRequest(requestId: UUID, pointOfSaleCreate: PointOfSaleCreate, pointOfSale: Option[PointOfSale] = None)
  case class PointOfSaleCreateResponse(requestId: UUID, pointOfSale: Option[PointOfSale] = None, messages: List[Message] = List(), failure: Option[Throwable] = None)

  case class PointOfSaleLoadRequest(requestId: UUID, id: Long)
  case class PointOfSaleLoadResponse(requestId: UUID, pointOfSale: Option[PointOfSale] = None, failure: Option[Throwable] = None)
}
