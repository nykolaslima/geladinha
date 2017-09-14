package com.zxventures.geladinha.infrastructure.routes

import com.zxventures.geladinha.infrastructure.serialization.ApplicationMarshalling
import com.zxventures.geladinha.resources.common.{Error, Message, Messages}
import com.zxventures.geladinha.resources.geometry.{LinearString, MultiPolygon, Point, Polygon}
import com.zxventures.geladinha.resources.pointOfSale.PointOfSale

trait ApplicationRoute extends ApplicationMarshalling with RequestIdDirective {
  implicit val pointOfSaleUnmarshaller = scalaPBFromRequestUnmarshaller(PointOfSale)
  implicit val messageUnmarshaller = scalaPBFromRequestUnmarshaller(Message)
  implicit val messagesUnmarshaller = scalaPBFromRequestUnmarshaller(Messages)
  implicit val errorUnmarshaller = scalaPBFromRequestUnmarshaller(Error)

  implicit val multiPolygonUnmarshaller = scalaPBFromRequestUnmarshaller(MultiPolygon)
  implicit val polygonUnmarshaller = scalaPBFromRequestUnmarshaller(Polygon)
  implicit val linearStringUnmarshaller = scalaPBFromRequestUnmarshaller(LinearString)
  implicit val pointUnmarshaller = scalaPBFromRequestUnmarshaller(Point)
}
